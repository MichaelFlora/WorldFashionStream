package com.flora.michael.wfcstream.model.response.broadcast

import com.flora.michael.wfcstream.model.resultCode.broadcast.StopBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StopStreamResponse (
    @SerializedName("result_code")
    val resultCodeCode: StopBroadcastResultCode
)