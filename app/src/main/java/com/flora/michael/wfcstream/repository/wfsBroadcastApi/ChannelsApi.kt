package com.flora.michael.wfcstream.repository.wfsBroadcastApi

import com.flora.michael.wfcstream.model.response.channels.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChannelsApi {
    @Multipart
    @POST("notify_channel_is_live")
    suspend fun notifyChannelIsLive(
        @Part("access_token") accessToken: String
    ): Response<NotifyChannelIsLiveResponse>

    @Multipart
    @POST("notify_channel_is_offline")
    suspend fun notifyChannelIsOffline(
        @Part("access_token") accessToken: String
    ): Response<NotifyChannelIsOfflineResponse>

    @GET("get_live_channels")
    suspend fun getLiveChannels(): Response<List<ChannelInformation>>

    @Multipart
    @POST("update_channel_title")
    suspend fun updateChannelTitle(
        @Part("access_token") accessToken: String,
        @Part("channel_title") newChannelTitle: String
    ): Response<UpdateBroadcastNameResponse>

    @Multipart
    @POST("start_watching_channel")
    suspend fun startWatchingChannel(
        @Part("access_token") accessToken: String,
        @Part("channel_id") channel_id: Long
    ): Response<StartWatchingChannelResponse>

    @Multipart
    @POST("stop_watching_channel")
    suspend fun stopWatchingChannel(
        @Part("access_token") accessToken: String,
        @Part("channel_id") channel_id: Long
    ): Response<StopWatchingChannelResponse>

    @Multipart
    @POST("get_channel_information")
    suspend fun getChannelInformation(
        @Part("access_token") accessToken: String,
        @Part("channel_id") channel_id: Long? = null
    ): Response<ChannelInformation>
}