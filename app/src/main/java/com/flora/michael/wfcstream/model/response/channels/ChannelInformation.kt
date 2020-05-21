package com.flora.michael.wfcstream.model.response.channels

import com.google.gson.annotations.SerializedName

data class ChannelInformation (
    @SerializedName("channel_id")
    val channelId: Long,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("broadcast_name")
    val broadcastName: String,

    @SerializedName("viewers_count")
    val viewersCount: Int,

    @SerializedName("is_online")
    val isOnline: Boolean
)