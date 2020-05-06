package com.flora.michael.wfcstream.repository.wfc_stream_api

import com.flora.michael.wfcstream.model.response.broadcast.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BroadcastsApi {
    @FormUrlEncoded
    @POST("notify_broadcast_started")
    suspend fun notifyBroadcastStarted(
        @Field("access_token") accessToken: String
    ): Response<StartStreamResponse>

    @FormUrlEncoded
    @POST("notify_broadcast_stopped")
    suspend fun notifyBroadcastStopped(
        @Field("access_token") accessToken: String
    ): Response<StopStreamResponse>

    @GET("get_live_broadcasts")
    suspend fun getLiveBroadcasts(): Response<List<BroadcastInformation>>

    @FormUrlEncoded
    @POST("get_own_broadcast_information")
    suspend fun getOwnBroadcastInformation(
        @Field("access_token") accessToken: String
    ): Response<OwnBroadcastInformation>

    @FormUrlEncoded
    @POST("update_broadcast_name")
    suspend fun updateBroadcastName(
        @Field("access_token") accessToken: String,
        @Field("broadcast_name") newBroadcastName: String
    ): Response<UpdateBroadcastNameResponse>
}