package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StartBroadcastResultCode
import com.google.gson.annotations.SerializedName

data class StartStreamResponse (
    @SerializedName("result_code")
    val resultCode: StartBroadcastResultCode
)