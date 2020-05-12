package com.flora.michael.wfcstream.model.response.broadcast

import com.google.gson.annotations.SerializedName

data class BroadcastInformation (
    @SerializedName("broadcast_id")
    val broadcastId: Long,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("broadcast_name")
    val broadcastName: String,

    @SerializedName("viewers_count")
    val viewersCount: Int,

    @SerializedName("is_online")
    val isOnline: Boolean
)