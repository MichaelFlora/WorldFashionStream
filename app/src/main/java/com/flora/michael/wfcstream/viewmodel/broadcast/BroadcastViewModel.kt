package com.flora.michael.wfcstream.viewmodel.broadcast

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

class BroadcastViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein: Kodein by closestKodein()
    private val authorizationRepository: AuthorizationRepository by instance()
    private val channelsRepository: ChannelsRepository by instance()

    private val viewersCountMutable = MutableLiveData(0)
    private val isBroadcastPlayingMutable = MutableLiveData<Boolean>(false)
    private val isSoundEnabledMutable = MutableLiveData<Boolean>(false)

    val isBroadcastPlaying: LiveData<Boolean> = isBroadcastPlayingMutable
    val isSoundEnabled: LiveData<Boolean> = isSoundEnabledMutable

    val viewersCount: LiveData<Int> = viewersCountMutable

    var channelId: Long = -1
        private set

    init{
        startRefreshViewersCount()
    }

    fun loadDataFromServer(channelId: Long){
        this.channelId = channelId

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                getWatchersCountFromServer(authorizationToken, channelId)

                setLoadingOperationFinished()
            }
        }
    }

    fun notifyUserStartedWatchingBroadcast(){
        if(channelId < 0)
            return

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            viewModelScope.launch {
                channelsRepository.startedWatchingBroadcast(authorizationToken, channelId)
            }
        }
    }

    fun notifyUserStoppedWatchingBroadcast(){
        if(channelId < 0)
            return

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            GlobalScope.launch {
                channelsRepository.stoppedWatchingBroadcast(authorizationToken, channelId)
            }
        }
    }

    private fun startRefreshViewersCount(){
        viewModelScope.launch(Dispatchers.Default) {
            while(isActive){
                delay(5000)
                authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
                    getWatchersCountFromServer(authorizationToken, channelId)
                }
            }
        }
    }

    private suspend fun getWatchersCountFromServer(authorizationToken: String, channelId: Long) = withContext(Dispatchers.Main){
        if(channelId >= 0){
            val viewersCount = channelsRepository.getChannelInformation(authorizationToken, channelId)?.watchersCount

            if(viewersCount != null){
                withContext(Dispatchers.Main){
                    viewersCountMutable.value = viewersCount
                }
            }
        }
    }

    fun changeBroadcastState(isPlaying: Boolean){
        isBroadcastPlayingMutable.value = isPlaying
    }

    fun changeSoundState(isEnabled: Boolean){
        isSoundEnabledMutable.value = isEnabled
    }
}