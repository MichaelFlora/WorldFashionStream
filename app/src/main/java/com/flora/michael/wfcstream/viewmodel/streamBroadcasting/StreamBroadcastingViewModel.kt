package com.flora.michael.wfcstream.viewmodel.streamBroadcasting

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.ChannelsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class StreamBroadcastingViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein: Kodein by closestKodein()
    private val channelsRepository: ChannelsRepository by instance()
    private val authorizationRepository: AuthorizationRepository by instance()

    private val viewersCountMutable = MutableLiveData(0)
    private val isBroadcastOnlineMutable = MutableLiveData<Boolean>()
    private val isMicrophoneActiveMutable = MutableLiveData<Boolean>(true)
    private val isCameraActiveMutable = MutableLiveData<Boolean>(true)
    private val broadcastNameMutable = MutableLiveData<String>()
    private val channelIdMutable = MutableLiveData<Long>()

    val viewersCount: LiveData<Int> = viewersCountMutable
    val isBroadcastOnline: LiveData<Boolean> = isBroadcastOnlineMutable
    val isMicrophoneActive: LiveData<Boolean> = isMicrophoneActiveMutable
    val isCameraActive: LiveData<Boolean> = isCameraActiveMutable
    val broadcastName: LiveData<String> = broadcastNameMutable
    val channelId: LiveData<Long> = channelIdMutable

    init{
        startRefreshViewersCount()
    }

    fun loadDataFromServer(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                val broadcastInformation = channelsRepository.getOwnChannelInformation(authorizationToken)
                isBroadcastOnlineMutable.value = broadcastInformation?.isOnline
                channelIdMutable.value = broadcastInformation?.channelId

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
                    channelId.value?.let{ channelId: Long ->
                        val viewersCount = channelsRepository.getChannelInformation(authorizationToken, channelId)?.watchersCount

                        if(viewersCount != null){
                            withContext(Dispatchers.Main){
                                viewersCountMutable.value = viewersCount
                            }
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
                    channelsRepository.notifyChannelIsLive(authorizationToken)
                } else{
                    channelsRepository.notifyChannelIsOffline(authorizationToken)
                }
            }
        }
    }

    fun isBroadcastInformationLoaded(): Boolean{
        return isBroadcastOnline.value != null && channelId.value != null
    }
}