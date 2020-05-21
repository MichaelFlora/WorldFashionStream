package com.flora.michael.wfcstream.ui.broadcast

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.flashphoner.fpwcsapi.Flashphoner
import com.flashphoner.fpwcsapi.bean.Connection
import com.flashphoner.fpwcsapi.bean.Data
import com.flashphoner.fpwcsapi.bean.StreamStatus
import com.flashphoner.fpwcsapi.bean.StreamStatusInfo
import com.flashphoner.fpwcsapi.constraints.AudioConstraints
import com.flashphoner.fpwcsapi.constraints.Constraints
import com.flashphoner.fpwcsapi.constraints.VideoConstraints
import com.flashphoner.fpwcsapi.layout.PercentFrameLayout
import com.flashphoner.fpwcsapi.session.*
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.ui.LoadableContentFragment
import com.flora.michael.wfcstream.view.ViewersCounterView
import com.flora.michael.wfcstream.viewmodel.broadcast.BroadcastViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

class BroadcastFragment: LoadableContentFragment(R.layout.stream_fragment) {
    private val viewModel by viewModels<BroadcastViewModel>()
    private val broadcastNavigationArguments: BroadcastFragmentArgs by navArgs()
    private val playerButtonsFadeOutAfterMilliseconds = 3000L

    private var webCallServerSession: Session? = null
    private var webCallServerBroadcast: Stream? = null

    private val handler = Handler()
    private val playerButtonsFadeOutRunnable = Runnable {
        playerButtonsContainer?.startAnimation(fadeOutAnimation)
    }

    private val fadeOutAnimation: Animation? by lazy {
        context?.let { contextNotNull ->
            AnimationUtils.loadAnimation(contextNotNull, R.anim.fade_out_fast).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        playerButtonsContainer?.visibility = View.GONE
                    }

                    override fun onAnimationStart(p0: Animation?) {}

                })
            }
        }
    }

    private var broadcastRendererContainer: PercentFrameLayout? = null
    private var broadcastRenderer: SurfaceViewRenderer? = null
    private var channelNameTextView: TextView? = null
    private var broadcastName: TextView? = null
    private var viewersCounterView: ViewersCounterView? = null

    private var playerButtonsContainer: ConstraintLayout? = null
    private var broadcastStateButton: MaterialButton? = null
    private var soundStateButton: MaterialButton? = null

    private val onBroadcastStatus: (Stream, StreamStatus) -> Unit = { broadcast, broadcastStatus ->

        val statusMessage = when(broadcastStatus){
            StreamStatus.PLAYING -> "Трансляция проигрывается"
            StreamStatus.NOT_ENOUGH_BANDWIDTH -> "Not enough bandwidth, consider using lower video resolution or bitrate."
            StreamStatus.FAILED -> {
                when(broadcast.info){
                    StreamStatusInfo.SESSION_DOES_NOT_EXIST -> "$broadcastStatus: Actual session does not exist"
                    StreamStatusInfo.STOPPED_BY_PUBLISHER_STOP -> "$broadcastStatus: Related publisher stopped its stream or lost connection"
                    StreamStatusInfo.SESSION_NOT_READY -> "$broadcastStatus: Session is not initialized or terminated on play ordinary stream"
                    StreamStatusInfo.RTSP_STREAM_NOT_FOUND -> "$broadcastStatus: Rtsp stream not found where agent received '404-Not Found'"
                    StreamStatusInfo.FAILED_TO_CONNECT_TO_RTSP_STREAM -> "$broadcastStatus: Failed to connect to rtsp stream"
                    StreamStatusInfo.FILE_NOT_FOUND -> "$broadcastStatus: File does not exist, check filename"
                    StreamStatusInfo.FILE_HAS_WRONG_FORMAT -> "$broadcastStatus: File has wrong format on play vod, this format is not supported"
                    StreamStatusInfo.TRANSCODING_REQUIRED_BUT_DISABLED -> "$broadcastStatus: Transcoding required, but disabled in settings"
                    else -> broadcast.info
                }
            }
            else -> broadcastStatus.toString()
        }

        Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        viewModel.initialize(broadcastNavigationArguments.channelId)
        connectToWebCallServer()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        showPlayerButtons()

        try{
            broadcastRenderer?.init(Flashphoner.context, null)
        } catch(ex: IllegalStateException){
            ex.printStackTrace()
        }

        if(webCallServerBroadcast?.status == StreamStatus.PLAYING){
            viewModel.notifyUserStartedWatchingBroadcast()
        }
    }

    override fun onPause() {
        super.onPause()
        broadcastRenderer?.release()
        viewModel.notifyUserStoppedWatchingBroadcast()
    }

    override fun onStop() {
        runBlocking {
            try{
                stopBroadcast()
                disconnectFromWebCallServer()
            } catch(ex: Exception){
                ex.printStackTrace()
            }
        }
        super.onStop()
    }

    private fun findAllViews(){
        view?.run{
            broadcastRendererContainer = findViewById(R.id.stream_renderer_container)
            broadcastRenderer = findViewById(R.id.stream_renderer)
            channelNameTextView = findViewById(R.id.stream_channel_name)
            broadcastName = findViewById(R.id.stream_title)
            viewersCounterView = findViewById(R.id.stream_fragment_viewers_count_view)

            playerButtonsContainer = findViewById(R.id.stream_fragment_player_buttons_container)
            broadcastStateButton = findViewById(R.id.stream_fragment_broadcast_state_button)
            //soundStateButton = findViewById(R.id.stream_fragment_sound_button)
        }
    }

    private fun initializeAllViews(){
        initializeChannelNameTextView()
        initializeBroadcastNameTextView()
        initializeVideoRenderer()
        initializePlayerButtonsContainer()
        initializeViewersCountView()
        initializeBroadcastStateObservation()
        initializeSoundStateObservation()
    }

    private fun initializeChannelNameTextView(){
        channelNameTextView?.text = broadcastNavigationArguments.channelName
    }

    private fun initializeBroadcastNameTextView(){
        broadcastName?.text = broadcastNavigationArguments.broadcastName
    }

    private fun initializeVideoRenderer(){
        broadcastRendererContainer?.setPosition(0,0,100,100)
        broadcastRenderer?.apply{
            setZOrderMediaOverlay(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setMirror(true)
            requestLayout()
        }

        broadcastRendererContainer?.setOnClickListener {
            showPlayerButtons()
        }
    }

    private fun initializePlayerButtonsContainer(){
        playerButtonsContainer?.setOnClickListener {
            showPlayerButtons()
        }

        for(child in playerButtonsContainer?.children ?: emptySequence()){
            child.setOnClickListener {
                showPlayerButtons()
            }
        }
    }

    private fun showPlayerButtons(){
        playerButtonsContainer?.visibility = View.VISIBLE
        handler.removeCallbacks(playerButtonsFadeOutRunnable)
        handler.postDelayed(playerButtonsFadeOutRunnable, playerButtonsFadeOutAfterMilliseconds)
    }

    private fun initializeViewersCountView(){
        viewModel.viewersCount.observe(viewLifecycleOwner, Observer{ viewersCount ->
            viewersCounterView?.viewersCount = viewersCount ?: 0
        })
    }

    private fun connectToWebCallServer(){
        webCallServerSession = createWebCallServerSession()
        webCallServerSession?.connect(Connection())
    }

    private fun disconnectFromWebCallServer(){
        webCallServerSession?.disconnect()
    }

    private fun createWebCallServerSession(): Session {
        val sessionOptions = SessionOptions(getString(R.string.web_call_server_url)).apply{
            remoteRenderer = broadcastRenderer
        }

        val session = Flashphoner.createSession(sessionOptions)

        session.on(object: SessionEvent {
            override fun onAppData(data: Data?) {

            }

            override fun onDisconnection(connection: Connection?) {

            }

            override fun onConnected(connection: Connection?) {
                lifecycleScope.launch(Dispatchers.Main){
                    changeBroadcastState(viewModel.isBroadcastPlaying.value ?: false)
                }
            }

            override fun onRegistered(connection: Connection?) {

            }

        })

        return session
    }

    private fun createWebCallServerBroadcast(webCallServerSession: Session?): Stream?{
        val streamOptions = StreamOptions(viewModel.channelId.toString())

        streamOptions.constraints = Constraints(
            AudioConstraints(),
            VideoConstraints()
        )

        val broadcast: Stream? = webCallServerSession?.createStream(streamOptions)

        broadcast?.on(onBroadcastStatus)

        return broadcast
    }

    private fun initializeBroadcastStateObservation(){
        viewModel.isBroadcastPlaying.observe(viewLifecycleOwner, Observer{ isPlaying: Boolean? ->
            changeBroadcastStateButtonIcon(isPlaying ?: false)
            changeBroadcastStateButtonAction(isPlaying ?: false)
        })
    }

    private fun initializeSoundStateObservation(){
        viewModel.isSoundEnabled.observe(viewLifecycleOwner, Observer{ isEnabled: Boolean? ->
            changeSoundStateButtonIcon(isEnabled ?: false)
            changeSoundStateButtonAction(isEnabled ?: false)
        })
    }

    private fun changeBroadcastStateButtonAction(isBroadcastActive: Boolean){
        broadcastStateButton?.setOnClickListener {
            changeBroadcastState(isBroadcastActive)
        }
    }

    private fun changeSoundStateButtonAction(isSoundEnabled: Boolean){
        soundStateButton?.setOnClickListener {
            changeSoundState(isSoundEnabled)
        }
    }

    private fun changeBroadcastState(isBroadcastActive: Boolean){
        viewModel.changeBroadcastState(!isBroadcastActive)

        when (isBroadcastActive) {
            true -> {
                stopBroadcast()
            }
            else -> {
                playBroadcast()
            }
        }
    }

    private fun changeSoundState(isSoundEnabled: Boolean){
        viewModel.changeSoundState(!isSoundEnabled)

        when (isSoundEnabled) {
            true -> {
                webCallServerBroadcast?.muteAudio()
            }
            else -> {
                webCallServerBroadcast?.unmuteAudio()
            }
        }
    }

    private fun playBroadcast(){
        stopBroadcast()
        webCallServerBroadcast = createWebCallServerBroadcast(webCallServerSession)
        webCallServerBroadcast?.play()
        changeSoundState(viewModel.isSoundEnabled.value ?: false)
    }

    private fun stopBroadcast(){
        try{
            webCallServerBroadcast?.stop()
        } catch (ex: Exception){
            ex.printStackTrace()
        }

        webCallServerBroadcast = null
    }

    private fun changeBroadcastStateButtonIcon(isBroadcastPlaying: Boolean){
        context?.let {
            broadcastStateButton?.icon = if(isBroadcastPlaying){
                ContextCompat.getDrawable(it, R.drawable.ic_pause_white_24dp)
            } else{
                ContextCompat.getDrawable(it, R.drawable.ic_play_arrow_white_24dp)
            }
        }
    }

    private fun changeSoundStateButtonIcon(isSoundEnabled: Boolean){
        context?.let {
            soundStateButton?.icon = if(isSoundEnabled){
                ContextCompat.getDrawable(it, R.drawable.ic_volume_off_white_24dp)
            } else{
                ContextCompat.getDrawable(it, R.drawable.ic_volume_up_white_24dp)
            }
        }
    }

}