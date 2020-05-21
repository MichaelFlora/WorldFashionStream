package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StoppedWatchingBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StoppedWatchingBroadcastResponse(
    @SerializedName("result_code")
    val resultCode: StoppedWatchingBroadcastResultCode
)