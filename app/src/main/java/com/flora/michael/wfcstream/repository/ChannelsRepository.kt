package com.flora.michael.wfcstream.repository

import com.flora.michael.wfcstream.model.response.channels.ChannelInformation
import com.flora.michael.wfcstream.model.resultCode.channels.*
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.ChannelsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ChannelsRepository(private val channelsApi: ChannelsApi) {

    suspend fun notifyBroadcastStarted(accessToken: String): StartBroadcastResultCode = withContext(Dispatchers.IO){
        var resultCode: StartBroadcastResultCode = StartBroadcastResultCode.DefaultError

        try{
            val response = channelsApi.notifyBroadcastStarted(accessToken)

            if(response.isSuccessful){
                response.body()?.let{ startStreamResponse ->
                    resultCode = startStreamResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun notifyBroadcastStopped(accessToken: String): StopBroadcastResultCode = withContext(Dispatchers.IO){
        var resultCodeCode: StopBroadcastResultCode = StopBroadcastResultCode.DefaultError

        try{
            val response = channelsApi.notifyBroadcastStopped(accessToken)

            if(response.isSuccessful){
                response.body()?.let{ stopStreamResponse ->
                    resultCodeCode = stopStreamResponse.resultCodeCode
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
            val response = channelsApi.getOwnChannelInformation(accessToken)

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
            val response = channelsApi.updateBroadcastName(accessToken, newBroadcastName)

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

    suspend fun startedWatchingBroadcast(accessToken: String, channelId: Long): StartedWatchingBroadcastResultCode? = withContext(Dispatchers.IO){
        var resultCode: StartedWatchingBroadcastResultCode = StartedWatchingBroadcastResultCode.DefaultError

        try{
            val response = channelsApi.startedWatchingBroadcast(accessToken, channelId)

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

    suspend fun stoppedWatchingBroadcast(accessToken: String, channelId: Long): StoppedWatchingBroadcastResultCode? = withContext(Dispatchers.IO){
        var resultCode: StoppedWatchingBroadcastResultCode = StoppedWatchingBroadcastResultCode.DefaultError

        try{
            val response = channelsApi.stoppedWatchingBroadcast(accessToken, channelId)

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