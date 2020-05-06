package com.flora.michael.wfcstream.model.response.broadcast

import com.flora.michael.wfcstream.model.resultCode.broadcast.StartBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StartStreamResponse (
    @SerializedName("result_code")
    val resultCode: StartBroadcastResultCode
)