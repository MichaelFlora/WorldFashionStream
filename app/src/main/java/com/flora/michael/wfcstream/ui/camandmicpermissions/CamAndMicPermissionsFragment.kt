package com.flora.michael.wfcstream.ui.camandmicpermissions

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.tools.checkPermission
import com.flora.michael.wfcstream.tools.requestPermission
import com.flora.michael.wfcstream.ui.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.camandmicpermissions.CamAndMicPermissionsViewModel
import com.google.android.material.button.MaterialButton

class CamAndMicPermissionsFragment: LoadableContentFragment(R.layout.cam_and_mic_permissions_fragment) {
    private val viewModel by viewModels<CamAndMicPermissionsViewModel>()

    private var switchOnCameraButton: MaterialButton? = null
    private var cameraIsSwitchedOnTextView: TextView? = null
    private var switchOnMicrophoneButton: MaterialButton? = null
    private var microphoneIsSwitchedOnTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        checkCamAndMicPermissions()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun findAllViews(){
        view?.run {
            switchOnCameraButton = findViewById(R.id.cam_and_mic_permissions_fragment_switch_on_camera)
            cameraIsSwitchedOnTextView = findViewById(R.id.cam_and_mic_permissions_fragment_camera_switched_on_text)
            switchOnMicrophoneButton = findViewById(R.id.cam_and_mic_permissions_fragment_switch_on_microphone)
            microphoneIsSwitchedOnTextView = findViewById(R.id.cam_and_mic_permissions_fragment_microphone_switched_on_text)
        }
    }

    private fun initializeAllViews(){
        initializeCameraPermissionObserver()
        initializeMicrophonePermissionObserver()
        initializePermissionsObserver()
        initializeSwitchOnCameraButton()
        initializeSwitchOnMicrophoneButton()
    }

    private fun checkCamAndMicPermissions(){
        activity?.let { parentActivity ->
            viewModel.setCameraPermissionGranted(checkPermission(parentActivity, Manifest.permission.CAMERA))
            viewModel.setMicrophonePermissionGranted(checkPermission(parentActivity, Manifest.permission.RECORD_AUDIO))
        }
    }

    private fun initializeCameraPermissionObserver(){
        viewModel.isCameraPermissionGranted.observe(viewLifecycleOwner, Observer { isCameraPermissionGranted ->
            if(isCameraPermissionGranted){
                switchOnCameraButton?.visibility = View.INVISIBLE
                cameraIsSwitchedOnTextView?.visibility = View.VISIBLE
            } else {
                cameraIsSwitchedOnTextView?.visibility = View.INVISIBLE
                switchOnCameraButton?.visibility = View.VISIBLE
            }
        })
    }

    private fun initializeMicrophonePermissionObserver(){
        viewModel.isMicrophonePermissionGranted.observe(viewLifecycleOwner, Observer { isMicrophonePermissionGranted ->
            if(isMicrophonePermissionGranted){
                switchOnMicrophoneButton?.visibility = View.INVISIBLE
                microphoneIsSwitchedOnTextView?.visibility = View.VISIBLE
            } else {
                microphoneIsSwitchedOnTextView?.visibility = View.INVISIBLE
                switchOnMicrophoneButton?.visibility = View.VISIBLE
            }
        })
    }

    private fun initializePermissionsObserver(){
        viewModel.arePermissionsGranted.observe(viewLifecycleOwner, Observer{ areAllPermissionsGranted ->
            if(areAllPermissionsGranted){
                val action = CamAndMicPermissionsFragmentDirections.actionDestinationCamAndMicPermissionsToDestinationStreamBroadcastingPermissions()
                navigationController.navigate(action)
            }
        })
    }

    private fun initializeSwitchOnCameraButton(){
        switchOnCameraButton?.setOnClickListener {
            activity?.let { parentActivity ->
                requestPermission(parentActivity,
                    Manifest.permission.CAMERA,
                    onDenied = {
                        viewModel.setCameraPermissionGranted(false)
                    },
                    onGranted = {
                        viewModel.setCameraPermissionGranted(true)
                    }
                )
            }
        }
    }

    private fun initializeSwitchOnMicrophoneButton(){
        switchOnMicrophoneButton?.setOnClickListener {
            activity?.let { parentActivity ->
                requestPermission(parentActivity,
                    Manifest.permission.RECORD_AUDIO,
                    onDenied = {
                        viewModel.setMicrophonePermissionGranted(false)
                    },
                    onGranted = {
                        viewModel.setMicrophonePermissionGranted(true)
                    }
                )
            }
        }
    }

}