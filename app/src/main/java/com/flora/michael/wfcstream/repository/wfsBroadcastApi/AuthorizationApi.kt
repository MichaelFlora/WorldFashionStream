package com.flora.michael.wfcstream.repository.wfsBroadcastApi

import com.flora.michael.wfcstream.model.response.authorization.LogInResponse
import com.flora.michael.wfcstream.model.response.authorization.LogOutResponse
import com.flora.michael.wfcstream.model.response.authorization.RegisterResponse
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthorizationApi{

    @Multipart
    @POST("login")
    suspend fun logIn(
        @Part("login") login: String,
        @Part("password") password: String
    ): Response<LogInResponse>

    @Multipart
    @POST("register")
    suspend fun register(
        @Part("login") login: String,
        @Part("password") password: String,
        @Part("user_name") userName: String
    ): Response<RegisterResponse>

    @Multipart
    @POST("logout")
    suspend fun logOut(
        @Part("access_token") accessToken: String
    ): Response<LogOutResponse>
}