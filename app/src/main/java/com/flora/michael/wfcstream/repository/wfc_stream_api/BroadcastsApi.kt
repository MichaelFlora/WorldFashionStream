package com.flora.michael.wfcstream.repository.wfc_stream_api

import com.flora.michael.wfcstream.model.response.broadcast.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BroadcastsApi {
    @Multipart
    @POST("notify_broadcast_started")
    suspend fun notifyBroadcastStarted(
        @Part("access_token") accessToken: String
    ): Response<StartStreamResponse>

    @Multipart
    @POST("notify_broadcast_stopped")
    suspend fun notifyBroadcastStopped(
        @Part("access_token") accessToken: String
    ): Response<StopStreamResponse>

    @GET("get_live_broadcasts")
    suspend fun getLiveBroadcasts(): Response<List<BroadcastInformation>>

    @Multipart
    @POST("get_own_broadcast_information")
    suspend fun getOwnBroadcastInformation(
        @Part("access_token") accessToken: String
    ): Response<BroadcastInformation>

    @Multipart
    @POST("update_broadcast_name")
    suspend fun updateBroadcastName(
        @Part("access_token") accessToken: String,
        @Part("broadcast_name") newBroadcastName: String
    ): Response<UpdateBroadcastNameResponse>

    @Multipart
    @POST("started_watching_broadcast")
    suspend fun startedWatchingBroadcast(
        @Part("access_token") accessToken: String,
        @Part("broadcast_id") broadcast_id: Long
    ): Response<StartedWatchingBroadcastResponse>

    @Multipart
    @POST("stopped_watching_broadcast")
    suspend fun stoppedWatchingBroadcast(
        @Part("access_token") accessToken: String,
        @Part("broadcast_id") broadcast_id: Long
    ): Response<StoppedWatchingBroadcastResponse>

    @Multipart
    @POST("get_broadcast_information")
    suspend fun getBroadcastInformation(
        @Part("access_token") accessToken: String,
        @Part("broadcast_id") broadcast_id: Long
    ): Response<BroadcastInformation>
}