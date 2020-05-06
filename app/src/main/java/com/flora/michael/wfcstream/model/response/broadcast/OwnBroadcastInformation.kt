package com.flora.michael.wfcstream.model.response.broadcast

import com.google.gson.annotations.SerializedName

data class OwnBroadcastInformation (
    @SerializedName("user_id")
    val userId: Long,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("broadcast_name")
    val broadcastName: String,

    @SerializedName("is_online")
    val isOnline: Boolean
)