package com.flora.michael.wfcstream.model.response.broadcast

import com.flora.michael.wfcstream.model.resultCode.broadcast.StoppedWatchingBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StoppedWatchingBroadcastResponse(
    @SerializedName("result_code")
    val resultCode: StoppedWatchingBroadcastResultCode
)