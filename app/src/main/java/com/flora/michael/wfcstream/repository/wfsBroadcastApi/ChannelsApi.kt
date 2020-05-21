package com.flora.michael.wfcstream.repository.wfsBroadcastApi

import com.flora.michael.wfcstream.model.response.channels.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChannelsApi {
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

    @GET("get_live_channels")
    suspend fun getLiveChannels(): Response<List<ChannelInformation>>

    @Multipart
    @POST("get_own_channel_information")
    suspend fun getOwnChannelInformation(
        @Part("access_token") accessToken: String
    ): Response<ChannelInformation>

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
        @Part("channel_id") channel_id: Long
    ): Response<StartedWatchingBroadcastResponse>

    @Multipart
    @POST("stopped_watching_broadcast")
    suspend fun stoppedWatchingBroadcast(
        @Part("access_token") accessToken: String,
        @Part("channel_id") channel_id: Long
    ): Response<StoppedWatchingBroadcastResponse>

    @Multipart
    @POST("get_channel_information")
    suspend fun getChannelInformation(
        @Part("access_token") accessToken: String,
        @Part("channel_id") channel_id: Long
    ): Response<ChannelInformation>
}