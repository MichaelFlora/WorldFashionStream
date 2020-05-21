package com.flora.michael.wfcstream.viewmodel.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.model.response.channels.ChannelInformation
import com.flora.michael.wfcstream.repository.ChannelsRepository
import com.flora.michael.wfcstream.viewmodel.DestinationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class HomeViewModel(application: Application): DestinationViewModel(application), KodeinAware{
    override val kodein by closestKodein()
    private val channelsRepository: ChannelsRepository by instance()
    private val activeChannelsMutable = MutableLiveData<List<ChannelInformation>>()
    private val isRefreshingInformationMutable = MutableLiveData<Boolean>()

    val activeChannels: LiveData<List<ChannelInformation>> = activeChannelsMutable
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
        activeChannelsMutable.value = channelsRepository.getLiveChannels()
    }

    private suspend fun getInactiveChannelsInformation() = withContext(Dispatchers.Main){
        // TODO: implement when needed
    }



}