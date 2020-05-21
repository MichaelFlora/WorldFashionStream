package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StartedWatchingBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StartedWatchingBroadcastResponse(
    @SerializedName("result_code")
    val resultCode: StartedWatchingBroadcastResultCode
)