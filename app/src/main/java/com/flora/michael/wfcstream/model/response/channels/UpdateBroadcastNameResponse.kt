package com.flora.michael.wfcstream.model.response.channels

import com.flora.michael.wfcstream.model.resultCode.channels.UpdateBroadcastNameResultCode
import com.google.gson.annotations.SerializedName

data class UpdateBroadcastNameResponse(
    @SerializedName("result_code")
    val resultCode: UpdateBroadcastNameResultCode
)