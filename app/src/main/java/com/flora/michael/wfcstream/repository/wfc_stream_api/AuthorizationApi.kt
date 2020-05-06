package com.flora.michael.wfcstream.repository.wfc_stream_api

import com.flora.michael.wfcstream.model.response.authorization.LogInResponse
import com.flora.michael.wfcstream.model.response.authorization.LogOutResponse
import com.flora.michael.wfcstream.model.response.authorization.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthorizationApi{

    @FormUrlEncoded
    @POST("login")
    suspend fun logIn(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<LogInResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("user_name") userName: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("logout")
    suspend fun logOut(
        @Field("access_token") accessToken: String
    ): Response<LogOutResponse>
}