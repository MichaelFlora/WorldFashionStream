package com.flora.michael.wfcstream.repository.wfsBroadcastApi

import com.flora.michael.wfcstream.model.response.session.SessionTokenResponse
import retrofit2.Response
import retrofit2.http.GET

interface SessionApi {
    @GET("new_session_token")
    suspend fun getCsrf(): Response<SessionTokenResponse>
}