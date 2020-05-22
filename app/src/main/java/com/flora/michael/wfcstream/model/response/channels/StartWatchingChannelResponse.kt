package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StartWatchingChannelResultCode
import com.google.gson.annotations.SerializedName

data class StartWatchingChannelResponse(
    @SerializedName("result_code")
    val resultCode: StartWatchingChannelResultCode
)