package com.flora.michael.wfcstream.repository

import com.flora.michael.wfcstream.model.response.channels.ChannelInformation
import com.flora.michael.wfcstream.model.resultCode.channels.*
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.ChannelsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChannelsRepository(private val channelsApi: ChannelsApi) {

    suspend fun notifyChannelIsLive(accessToken: String): NotifyChannelIsLiveResultCode = withContext(Dispatchers.IO){
        var resultCode: NotifyChannelIsLiveResultCode = NotifyChannelIsLiveResultCode.DefaultError

        try{
            val response = channelsApi.notifyChannelIsLive(accessToken)

            if(response.isSuccessful){
                response.body()?.let{ notifyChannelIsLiveResponse ->
                    resultCode = notifyChannelIsLiveResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun notifyChannelIsOffline(accessToken: String): NotifyChannelIsOfflineResultCode = withContext(Dispatchers.IO){
        var resultCodeCode: NotifyChannelIsOfflineResultCode = NotifyChannelIsOfflineResultCode.DefaultError

        try{
            val response = channelsApi.notifyChannelIsOffline(accessToken)

            if(response.isSuccessful){
                response.body()?.let{ notifyChannelIsOfflineResponse ->
                    resultCodeCode = notifyChannelIsOfflineResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCodeCode
    }

    suspend fun getLiveChannels(): List<ChannelInformation> = withContext(Dispatchers.IO){
        var liveChannels: List<ChannelInformation> = emptyList()

        try{
            val response = channelsApi.getLiveChannels()

            if(response.isSuccessful){
                liveChannels = response.body() ?: emptyList()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        liveChannels
    }

    suspend fun getOwnChannelInformation(accessToken: String): ChannelInformation? = withContext(Dispatchers.IO){
        var ownChannelInformation: ChannelInformation? = null

        try{
            val response = channelsApi.getChannelInformation(accessToken)

            if(response.isSuccessful){
                ownChannelInformation = response.body()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        ownChannelInformation
    }

    suspend fun updateBroadcastName(accessToken: String, newBroadcastName: String): UpdateBroadcastNameResultCode? = withContext(Dispatchers.IO){
        var resultCode: UpdateBroadcastNameResultCode = UpdateBroadcastNameResultCode.DefaultError

        try{
            val response = channelsApi.updateChannelTitle(accessToken, newBroadcastName)

            if(response.isSuccessful){
                response.body()?.let{ updateBroadcastNameResponse ->
                    resultCode = updateBroadcastNameResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun startedWatchingBroadcast(accessToken: String, channelId: Long): StartWatchingChannelResultCode? = withContext(Dispatchers.IO){
        var resultCode: StartWatchingChannelResultCode = StartWatchingChannelResultCode.DefaultError

        try{
            val response = channelsApi.startWatchingChannel(accessToken, channelId)

            if(response.isSuccessful){
                response.body()?.let{ startedWatchingBroadcastResponse ->
                    resultCode = startedWatchingBroadcastResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun stoppedWatchingBroadcast(accessToken: String, channelId: Long): StopWatchingChannelResultCode? = withContext(Dispatchers.IO){
        var resultCode: StopWatchingChannelResultCode = StopWatchingChannelResultCode.DefaultError

        try{
            val response = channelsApi.stopWatchingChannel(accessToken, channelId)

            if(response.isSuccessful){
                response.body()?.let{ stoppedWatchingBroadcastResponse ->
                    resultCode = stoppedWatchingBroadcastResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun getChannelInformation(accessToken: String, channelId: Long): ChannelInformation? = withContext(Dispatchers.IO){
        var channelInformation: ChannelInformation? = null

        try{
            val response = channelsApi.getChannelInformation(accessToken, channelId)

            if(response.isSuccessful){
                channelInformation = response.body()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        channelInformation
    }

}