package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StopBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StopStreamResponse (
    @SerializedName("result_code")
    val resultCodeCode: StopBroadcastResultCode
)