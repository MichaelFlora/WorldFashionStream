package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.StopWatchingChannelResultCode
import com.google.gson.annotations.SerializedName

data class StopWatchingChannelResponse(
    @SerializedName("result_code")
    val resultCode: StopWatchingChannelResultCode
)