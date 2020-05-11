package com.flora.michael.wfcstream.viewmodel.streamBroadcasting

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class StreamBroadcastingViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein: Kodein by closestKodein()
    private val broadcastsRepository: BroadcastsRepository by instance()
    private val authorizationRepository: AuthorizationRepository by instance()

    private val isBroadcastOnlineMutable = MutableLiveData<Boolean>()
    private val broadcastNameMutable = MutableLiveData<String>()
    private val userIdMutable = MutableLiveData<Long>()

    val isBroadcastOnline: LiveData<Boolean> = isBroadcastOnlineMutable
    val broadcastName: LiveData<String> = broadcastNameMutable
    val userId: LiveData<Long> = userIdMutable

    fun loadDataFromServer(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                val broadcastInformation = broadcastsRepository.getOwnBroadcastInformation(authorizationToken)
                isBroadcastOnlineMutable.value = broadcastInformation?.isOnline
                userIdMutable.value = broadcastInformation?.userId

                setLoadingOperationFinished()
            }
        }
    }

    fun notifyViewersAboutBroadcastState(isOnline: Boolean){
        isBroadcastOnlineMutable.value = isOnline

        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            GlobalScope.launch {
                if(isOnline){
                    broadcastsRepository.broadcastStarted(authorizationToken)
                } else{
                    broadcastsRepository.broadcastStopped(authorizationToken)
                }
            }
        }
    }

    fun isBroadcastInformationLoaded(): Boolean{
        return isBroadcastOnline.value != null && userId.value != null
    }
}