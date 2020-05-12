package com.flora.michael.wfcstream.viewmodel.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.model.response.broadcast.BroadcastInformation
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class HomeViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein by closestKodein()
    private val broadcastsRepository: BroadcastsRepository by instance()
    private val activeChannelsMutable = MutableLiveData<List<BroadcastInformation>>()
    private val isRefreshingInformationMutable = MutableLiveData<Boolean>()

    val activeChannels: LiveData<List<BroadcastInformation>> = activeChannelsMutable
    val isRefreshing: LiveData<Boolean> = isRefreshingInformationMutable

    fun getChannelsInformationFromServer(){
        setLoadingOperationStarted()

        viewModelScope.launch(Dispatchers.Main){
            val activeChannelsLoadingJob = launch { getActiveChannelsInformation() }
            val inactiveChannelsLoadingJob = launch { getInactiveChannelsInformation() }

            activeChannelsLoadingJob.join()
            inactiveChannelsLoadingJob.join()

            setLoadingOperationFinished()
        }
    }

    fun isChannelsInformationLoaded(): Boolean{
        return activeChannels.value != null
    }

    fun refreshChannelsInformation(){
        isRefreshingInformationMutable.value = true
        viewModelScope.launch(Dispatchers.Main) {
            val activeChannelsLoadingJob = launch { getActiveChannelsInformation() }
            val inactiveChannelsLoadingJob = launch { getInactiveChannelsInformation() }

            activeChannelsLoadingJob.join()
            inactiveChannelsLoadingJob.join()

            isRefreshingInformationMutable.value = false
        }
    }

    private suspend fun getActiveChannelsInformation() = withContext(Dispatchers.Main) {
        activeChannelsMutable.value = broadcastsRepository.getLiveBroadcasts()
    }

    private suspend fun getInactiveChannelsInformation() = withContext(Dispatchers.Main){
        // TODO: implement when needed
    }



}