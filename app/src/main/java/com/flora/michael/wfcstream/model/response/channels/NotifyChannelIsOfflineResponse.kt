package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.NotifyChannelIsOfflineResultCode
import com.google.gson.annotations.SerializedName

data class NotifyChannelIsOfflineResponse (
    @SerializedName("result_code")
    val resultCode: NotifyChannelIsOfflineResultCode
)