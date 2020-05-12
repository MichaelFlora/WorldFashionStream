package com.flora.michael.wfcstream.repository

import com.flora.michael.wfcstream.model.response.broadcast.BroadcastInformation
import com.flora.michael.wfcstream.model.resultCode.broadcast.*
import com.flora.michael.wfcstream.repository.wfc_stream_api.BroadcastsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class BroadcastsRepository(private val broadcastsApi: BroadcastsApi) {

    suspend fun notifyBroadcastStarted(accessToken: String): StartBroadcastResultCode = withContext(Dispatchers.IO){
        var resultCode: StartBroadcastResultCode = StartBroadcastResultCode.DefaultError

        try{
            val response = broadcastsApi.notifyBroadcastStarted(accessToken)

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
            val response = broadcastsApi.notifyBroadcastStopped(accessToken)

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

    suspend fun getLiveBroadcasts(): List<BroadcastInformation> = withContext(Dispatchers.IO){
        var liveBroadcasts: List<BroadcastInformation> = emptyList()

        try{
            val response = broadcastsApi.getLiveBroadcasts()

            if(response.isSuccessful){
                liveBroadcasts = response.body() ?: emptyList()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        liveBroadcasts
    }

    suspend fun getOwnBroadcastInformation(accessToken: String): BroadcastInformation? = withContext(Dispatchers.IO){
        var ownBroadcastInformation: BroadcastInformation? = null

        try{
            val response = broadcastsApi.getOwnBroadcastInformation(accessToken)

            if(response.isSuccessful){
                ownBroadcastInformation = response.body()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        ownBroadcastInformation
    }

    suspend fun updateBroadcastName(accessToken: String, newBroadcastName: String): UpdateBroadcastNameResultCode? = withContext(Dispatchers.IO){
        var resultCode: UpdateBroadcastNameResultCode = UpdateBroadcastNameResultCode.DefaultError

        try{
            val response = broadcastsApi.updateBroadcastName(accessToken, newBroadcastName)

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

    suspend fun startedWatchingBroadcast(accessToken: String, broadcastId: Long): StartedWatchingBroadcastResultCode? = withContext(Dispatchers.IO){
        var resultCode: StartedWatchingBroadcastResultCode = StartedWatchingBroadcastResultCode.DefaultError

        try{
            val response = broadcastsApi.startedWatchingBroadcast(accessToken, broadcastId)

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

    suspend fun stoppedWatchingBroadcast(accessToken: String, broadcastId: Long): StoppedWatchingBroadcastResultCode? = withContext(Dispatchers.IO){
        var resultCode: StoppedWatchingBroadcastResultCode = StoppedWatchingBroadcastResultCode.DefaultError

        try{
            val response = broadcastsApi.stoppedWatchingBroadcast(accessToken, broadcastId)

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

    suspend fun getBroadcastInformation(accessToken: String, broadcastId: Long): BroadcastInformation? = withContext(Dispatchers.IO){
        var broadcastInformation: BroadcastInformation? = null

        try{
            val response = broadcastsApi.getBroadcastInformation(accessToken, broadcastId)

            if(response.isSuccessful){
                broadcastInformation = response.body()
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        broadcastInformation
    }

}