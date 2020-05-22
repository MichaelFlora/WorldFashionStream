package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.NotifyChannelIsLiveResultCode
import com.google.gson.annotations.SerializedName

data class NotifyChannelIsLiveResponse (
    @SerializedName("result_code")
    val resultCode: NotifyChannelIsLiveResultCode
)