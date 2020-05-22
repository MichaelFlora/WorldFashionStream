package com.flora.michael.wfcstream.model.response.channels

import com.google.gson.annotations.SerializedName

data class ChannelInformation (
    @SerializedName("channel_id")
    val channelId: Long,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("channel_title")
    val channelTitle: String,

    @SerializedName("watchers_count")
    val watchersCount: Int,

    @SerializedName("is_online")
    val isOnline: Boolean
)