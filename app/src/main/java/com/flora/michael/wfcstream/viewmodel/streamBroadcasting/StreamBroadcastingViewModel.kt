package com.flora.michael.wfcstream.viewmodel.streamBroadcasting

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class StreamBroadcastingViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein: Kodein by closestKodein()
    private val broadcastsRepository: BroadcastsRepository by instance()
    private val authorizationRepository: AuthorizationRepository by instance()

    private val viewersCountMutable = MutableLiveData(0)
    private val isBroadcastOnlineMutable = MutableLiveData<Boolean>()
    private val isMicrophoneActiveMutable = MutableLiveData<Boolean>(true)
    private val isCameraActiveMutable = MutableLiveData<Boolean>(true)
    private val broadcastNameMutable = MutableLiveData<String>()
    private val broadcastIdMutable = MutableLiveData<Long>()

    val viewersCount: LiveData<Int> = viewersCountMutable
    val isBroadcastOnline: LiveData<Boolean> = isBroadcastOnlineMutable
    val isMicrophoneActive: LiveData<Boolean> = isMicrophoneActiveMutable
    val isCameraActive: LiveData<Boolean> = isCameraActiveMutable
    val broadcastName: LiveData<String> = broadcastNameMutable
    val broadcastId: LiveData<Long> = broadcastIdMutable

    init{
        startRefreshViewersCount()
    }

    fun loadDataFromServer(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                val broadcastInformation = broadcastsRepository.getOwnBroadcastInformation(authorizationToken)
                isBroadcastOnlineMutable.value = broadcastInformation?.isOnline
                broadcastIdMutable.value = broadcastInformation?.broadcastId

                setLoadingOperationFinished()
            }
        }
    }

    fun changeCameraState(isActive: Boolean){
        isCameraActiveMutable.value = isActive
    }

    fun changeMicrophoneState(isActive: Boolean){
        isMicrophoneActiveMutable.value = isActive
    }

    private fun startRefreshViewersCount(){
        viewModelScope.launch(Dispatchers.Default) {
            while(isActive){
                delay(5000)
                authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
                    broadcastId.value?.let{ broadcastId: Long ->
                        val viewersCount = broadcastsRepository.getBroadcastInformation(authorizationToken, broadcastId)?.viewersCount

                        if(viewersCount != null){
                            viewersCountMutable.value = viewersCount
                        }
                    }
                }
            }
        }
    }

    fun notifyViewersAboutBroadcastState(isOnline: Boolean){
        isBroadcastOnlineMutable.value = isOnline

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            GlobalScope.launch {
                if(isOnline){
                    broadcastsRepository.notifyBroadcastStarted(authorizationToken)
                } else{
                    broadcastsRepository.notifyBroadcastStopped(authorizationToken)
                }
            }
        }
    }

    fun isBroadcastInformationLoaded(): Boolean{
        return isBroadcastOnline.value != null && broadcastId.value != null
    }
}