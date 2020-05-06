package com.flora.michael.wfcstream.model.response.authorization

import com.flora.michael.wfcstream.model.resultCode.authorization.LogOutResultCode
import com.google.gson.annotations.SerializedName

data class LogOutResponse(
    @SerializedName("result_code")
    val resultCode: LogOutResultCode
)