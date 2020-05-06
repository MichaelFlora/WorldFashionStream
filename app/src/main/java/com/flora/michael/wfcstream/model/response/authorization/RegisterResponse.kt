package com.flora.michael.wfcstream.model.response.authorization

import com.flora.michael.wfcstream.model.resultCode.authorization.RegisterResultCode
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("result_code")
    val resultCode: RegisterResultCode
)