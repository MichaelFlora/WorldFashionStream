package com.flora.michael.wfcstream.viewmodel.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.ChannelsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class ProfileViewModel(application: Application): DestinationViewModel(application) {
    private val authorizationRepository: AuthorizationRepository by instance()
    private val channelsRepository: ChannelsRepository by instance()

    private val userNameMutable = MutableLiveData<String>()
    private val broadcastNameMutable = MutableLiveData<String>()
    private val isChannelOnlineMutable = MutableLiveData<Boolean>()

    val userName: LiveData<String> = userNameMutable
    val broadcastName: LiveData<String> = broadcastNameMutable
    val isChannelOnline: LiveData<Boolean> = isChannelOnlineMutable

    fun getChannelInformationFromServer(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                val channelInformation = channelsRepository.getOwnChannelInformation(authorizationToken)
                userNameMutable.value = channelInformation?.userName
                broadcastNameMutable.value = channelInformation?.broadcastName
                isChannelOnlineMutable.value = channelInformation?.isOnline
                setLoadingOperationFinished()
            }
        }
    }

    fun isChannelInformationLoaded(): Boolean{
        return userNameMutable.value != null &&
                broadcastNameMutable.value != null &&
                isChannelOnlineMutable.value != null
    }

    fun updateBroadcastName(broadcastName: String){
        if(broadcastNameMutable.value?.equals(broadcastName) == true)
            return

        broadcastNameMutable.value = broadcastName
    }

    fun saveBroadcastName(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->
            broadcastNameMutable.value?.let { newBroadcastName ->
                viewModelScope.launch {
                    channelsRepository.updateBroadcastName(authorizationToken, newBroadcastName)
                }
            }
        }
    }

    fun logOut(){
        viewModelScope.launch {
            authorizationRepository.logOut()
        }
    }
}