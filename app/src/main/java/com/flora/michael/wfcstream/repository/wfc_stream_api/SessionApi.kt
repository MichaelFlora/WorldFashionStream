package com.flora.michael.wfcstream.repository.wfc_stream_api

import com.flora.michael.wfcstream.model.response.session.SessionTokenResponse
import retrofit2.Response
import retrofit2.http.POST

interface SessionApi {
    @POST("get_csrf")
    suspend fun getCsrf(): Response<SessionTokenResponse>
}