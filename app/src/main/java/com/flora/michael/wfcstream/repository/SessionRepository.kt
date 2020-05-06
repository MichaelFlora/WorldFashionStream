package com.flora.michael.wfcstream.repository

import com.flora.michael.wfcstream.repository.wfc_stream_api.SessionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SessionRepository(private val sessionApi: SessionApi) {
    var currentSessionToken: String? = null
        private set

    suspend fun updateSessionToken(): String? = withContext(Dispatchers.IO){
        var resultToken: String? = null

        try{
            val response = sessionApi.getCsrf()

            if(response.isSuccessful){
                resultToken = response.body()?.sessionToken
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        currentSessionToken = resultToken

        resultToken
    }
}