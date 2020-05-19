package com.flora.michael.wfcstream.viewmodel.stream

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class StreamViewModel(application: Application): AndroidViewModel(application), KodeinAware{
    override val kodein: Kodein by closestKodein()
    private val authorizationRepository: AuthorizationRepository by instance()
    private val broadcastsRepository: BroadcastsRepository by instance()

    private val viewersCountMutable = MutableLiveData(0)
    private val isBroadcastPlayingMutable = MutableLiveData<Boolean>(false)
    private val isSoundEnabledMutable = MutableLiveData<Boolean>(false)

    val isBroadcastPlaying: LiveData<Boolean> = isBroadcastPlayingMutable
    val isSoundEnabled: LiveData<Boolean> = isSoundEnabledMutable

    val viewersCount: LiveData<Int> = viewersCountMutable

    var broadcastId: Long = -1
        private set

    init{
        startRefreshViewersCount()
    }

    fun initialize(broadcastId: Long){
        this.broadcastId = broadcastId
    }

    fun notifyUserStartedWatchingBroadcast(){
        if(broadcastId < 0)
            return

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            viewModelScope.launch {
                broadcastsRepository.startedWatchingBroadcast(authorizationToken, broadcastId)
            }
        }
    }

    fun notifyUserStoppedWatchingBroadcast(){
        if(broadcastId < 0)
            return

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            viewModelScope.launch {
                broadcastsRepository.stoppedWatchingBroadcast(authorizationToken, broadcastId)
            }
        }
    }

    private fun startRefreshViewersCount(){
        viewModelScope.launch(Dispatchers.Default) {
            while(isActive){
                delay(5000)
                authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
                    if(broadcastId >= 0){
                        val viewersCount = broadcastsRepository.getBroadcastInformation(authorizationToken, broadcastId)?.viewersCount
                        Log.e("TEST", "TYT")

                        if(viewersCount != null){
                            viewersCountMutable.value = viewersCount
                        }
                    }
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