package com.flora.michael.wfcstream.model.response.authorization

import com.flora.michael.wfcstream.model.resultCode.authorization.LogInResultCode
import com.google.gson.annotations.SerializedName

data class LogInResponse(
    @SerializedName("result_code")
    val resultCode: LogInResultCode,

    @SerializedName("access_token")
    val accessToken: String
)