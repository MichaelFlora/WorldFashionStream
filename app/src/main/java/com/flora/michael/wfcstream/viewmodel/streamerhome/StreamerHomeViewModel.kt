package com.flora.michael.wfcstream.viewmodel.streamerhome

import android.app.Application
import androidx.lifecycle.*
import com.flora.michael.wfcstream.model.resultCode.authorization.LogOutResultCode
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class StreamerHomeViewModel(application: Application): DestinationViewModel(application) {
    private val authorizationRepository: AuthorizationRepository by instance()
    private val broadcastsRepository: BroadcastsRepository by instance()

    private val userNameMutable = MutableLiveData<String>()
    private val broadcastNameMutable = MutableLiveData<String>()
    private val isBroadcastOnlineMutable = MutableLiveData<Boolean>()

    val userName: LiveData<String> = userNameMutable
    val broadcastName: LiveData<String> = broadcastNameMutable
    val isBroadcastOnline: LiveData<Boolean> = isBroadcastOnlineMutable

    fun getBroadcastInformationFromServer(){
        authorizationRepository.currentAccessToken.value?.let { authorizationToken ->

            setLoadingOperationStarted()

            viewModelScope.launch {
                val broadcastInformation = broadcastsRepository.getOwnBroadcastInformation(authorizationToken)
                userNameMutable.value = broadcastInformation?.userName
                broadcastNameMutable.value = broadcastInformation?.broadcastName
                isBroadcastOnlineMutable.value = broadcastInformation?.isOnline
                setLoadingOperationFinished()
            }
        }
    }

    fun isBroadcastInformationLoaded(): Boolean{
        return userNameMutable.value != null &&
                broadcastNameMutable.value != null &&
                isBroadcastOnlineMutable.value != null
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
                    broadcastsRepository.updateBroadcastName(authorizationToken, newBroadcastName)
                }
            }
        }
    }

    fun logOut(){
        viewModelScope.launch {
            val result = authorizationRepository.logOut()
        }
    }
}