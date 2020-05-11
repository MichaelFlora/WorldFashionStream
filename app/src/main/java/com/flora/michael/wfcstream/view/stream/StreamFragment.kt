package com.flora.michael.wfcstream.view.stream

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
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
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.stream.StreamViewModel
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

class StreamFragment: LoadableContentFragment(R.layout.stream_fragment) {
    private val viewModel by viewModels<StreamViewModel>()
    private val broadcastNavigationArguments: StreamFragmentArgs by navArgs()

    private var webCallServerSession: Session? = null
    private var webCallServerBroadcast: Stream? = null

    private var broadcastRendererContainer: PercentFrameLayout? = null
    private var broadcastRenderer: SurfaceViewRenderer? = null
    private var channelNameTextView: TextView? = null
    private var broadcastTitle: TextView? = null

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
        viewModel.initialize(broadcastNavigationArguments.broadcastId)
        connectToWebCallServer()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        try{
            broadcastRenderer?.init(Flashphoner.context, null)
        } catch(ex: IllegalStateException){
            ex.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        broadcastRenderer?.release()
    }

    override fun onDestroy() {
        try{
            webCallServerBroadcast?.stop()
            disconnectFromWebCallServer()
        } catch(ex: Exception){
            ex.printStackTrace()
        }

        super.onDestroy()
    }

    private fun findAllViews(){
        view?.run{
            broadcastRendererContainer = findViewById(R.id.stream_renderer_container)
            broadcastRenderer = findViewById(R.id.stream_renderer)
            channelNameTextView = findViewById(R.id.stream_channel_name)
            broadcastTitle = findViewById(R.id.stream_title)
        }
    }

    private fun initializeAllViews(){
        initializeChannelNameTextView()
        initializeBroadcastTitleTextView()
        initializeVideoRenderer()
    }

    private fun initializeChannelNameTextView(){
        channelNameTextView?.text = broadcastNavigationArguments.channelName
    }

    private fun initializeBroadcastTitleTextView(){
        broadcastTitle?.text = broadcastNavigationArguments.broadcastTitle
    }

    private fun initializeVideoRenderer(){
        broadcastRendererContainer?.setPosition(0,0,100,100)
        broadcastRenderer?.apply{
            setZOrderMediaOverlay(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setMirror(true)
            requestLayout()
        }
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
                getWebCallServerBroadcast()
                webCallServerBroadcast?.play()
            }

            override fun onRegistered(connection: Connection?) {

            }

        })

        return session
    }

    private fun getWebCallServerBroadcast(){
        val streamOptions = StreamOptions(viewModel.broadcastId.toString())

        streamOptions.constraints = Constraints(
            AudioConstraints(),
            VideoConstraints().apply {
                videoFps = 60
                setResolution(720, 1280)
            }
        )

        webCallServerBroadcast = webCallServerSession?.createStream(streamOptions)

        webCallServerBroadcast?.on(onBroadcastStatus)
    }

}