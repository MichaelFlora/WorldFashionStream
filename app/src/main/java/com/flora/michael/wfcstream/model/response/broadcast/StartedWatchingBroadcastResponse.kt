package com.flora.michael.wfcstream.model.response.broadcast

import com.flora.michael.wfcstream.model.resultCode.broadcast.StartedWatchingBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StartedWatchingBroadcastResponse(
    @SerializedName("result_code")
    val resultCode: StartedWatchingBroadcastResultCode
)