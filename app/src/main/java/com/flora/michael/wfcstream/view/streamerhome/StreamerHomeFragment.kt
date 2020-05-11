package com.flora.michael.wfcstream.view.streamerhome

import android.Manifest
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.ui.onNavDestinationSelected
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.tools.checkPermissions
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.streamerhome.StreamerHomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class StreamerHomeFragment: LoadableContentFragment(R.layout.streamer_home_fragment) {
    private val viewModel by viewModels<StreamerHomeViewModel>()

    private var userNameTextView: TextView? = null
    private var broadcastNameEditText: TextInputEditText? = null
    private var startBroadcastButton: MaterialButton? = null
    //private var logOutButton: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findAllViews()
        initializeAllViews()
        viewModel.getBroadcastInformationFromServer()
    }

    override fun onPause() {
        viewModel.saveBroadcastName()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navigationController) ||
                when(item.itemId){
                    R.id.streamer_home_fragment_log_out -> {
                        viewModel.logOut()
                        true
                    }
                    else -> false
                } ||
                super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.streamer_home_menu, menu)
    }

    private fun findAllViews(){
        view?.apply {
            userNameTextView = findViewById(R.id.streamer_home_fragment_user_name)
            broadcastNameEditText = findViewById(R.id.streamer_home_fragment_stream_title_edit_text)
            startBroadcastButton = findViewById(R.id.streamer_home_fragment_start_broadcast)
            //logOutButton = findViewById(R.id.streamer_home_fragment_log_out)
        }
    }

    private fun initializeAllViews(){
        initializeContentLoadingObservation()
        initializeUserNameTextView()
        initializeBroadcastNameEditText()
        initializeStartBroadcastButton()
        //initializeLogOutButton()
    }

    private fun initializeContentLoadingObservation(){
        viewModel.isContentLoading.observe(viewLifecycleOwner, Observer { isContentLoading ->
            when{
                isContentLoading -> showLoadingProgressBar(withHiddenContent = true)
                viewModel.isBroadcastInformationLoaded() ->
                    hideLoadingProgressBar()
                else ->
                    hideLoadingProgressBar(withError = true)
            }
        })
    }

    private fun initializeUserNameTextView(){
        viewModel.userName.observe(viewLifecycleOwner, Observer { userName ->
            userNameTextView?.text = userName
        })
    }

    private fun initializeBroadcastNameEditText(){
        viewModel.broadcastName.observe(viewLifecycleOwner, Observer{ broadcastName ->
            if(broadcastNameEditText?.text?.toString().equals(broadcastName))
                return@Observer

            broadcastNameEditText?.setText(broadcastName)
        })

        broadcastNameEditText?.addTextChangedListener { editable ->
            viewModel.updateBroadcastName(editable.toString())
        }
    }

    private fun initializeStartBroadcastButton(){
        viewModel.isBroadcastOnline.observe(viewLifecycleOwner, Observer{ isBroadcastOnline: Boolean? ->
            if(isBroadcastOnline == true){
                startBroadcastButton?.isEnabled = false
            }
        })

        startBroadcastButton?.setOnClickListener {
            activity?.let {
                val action = if(checkPermissions(it, listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))){
                    StreamerHomeFragmentDirections.actionDestinationStreamerHomeToDestinationStreamBroadcasting()
                } else{
                    StreamerHomeFragmentDirections.actionDestinationStreamerHomeToDestinationCamAndMicPermissions()
                }

                navigationController.navigate(action)
            }
        }
    }

//    private fun initializeLogOutButton(){
//        logOutButton?.setOnClickListener {
//            viewModel.logOut()
//        }
//    }
}