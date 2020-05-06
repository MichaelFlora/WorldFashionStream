package com.flora.michael.wfcstream.model.response.broadcast

import com.flora.michael.wfcstream.model.resultCode.broadcast.UpdateBroadcastNameResultCode
import com.google.gson.annotations.SerializedName

data class UpdateBroadcastNameResponse(
    @SerializedName("result_code")
    val resultCode: UpdateBroadcastNameResultCode
)