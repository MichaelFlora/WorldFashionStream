package com.flora.michael.wfcstream.viewmodel.camandmicpermissions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.karumi.dexter.listener.PermissionGrantedResponse

class CamAndMicPermissionsViewModel(application: Application): AndroidViewModel(application){
    private val isCameraPermissionGrantedMutable = MutableLiveData<Boolean>()
    private val isMicrophonePermissionGrantedMutable = MutableLiveData<Boolean>()

    val isCameraPermissionGranted: LiveData<Boolean> = isCameraPermissionGrantedMutable
    val isMicrophonePermissionGranted: LiveData<Boolean> = isMicrophonePermissionGrantedMutable
    val arePermissionsGranted: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val onChanged: (Any?) -> Unit = {
            value = isCameraPermissionGranted.value ?: false && isMicrophonePermissionGranted.value  ?: false
        }

        addSource(isCameraPermissionGrantedMutable, onChanged)
        addSource(isMicrophonePermissionGrantedMutable, onChanged)
    }

    fun setCameraPermissionGranted(isGranted: Boolean){
        isCameraPermissionGrantedMutable.value = isGranted
    }

    fun setMicrophonePermissionGranted(isGranted: Boolean){
        isMicrophonePermissionGrantedMutable.value = isGranted
    }
}