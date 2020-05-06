package com.flora.michael.wfcstream.model.resultCode.authorization

import androidx.annotation.StringRes
import com.flora.michael.wfcstream.R
import com.google.gson.annotations.SerializedName

enum class LogInResultCode(
    @StringRes
    val description: Int
){
    @SerializedName("0")
    Success(R.string.log_in_result_code_success),
    @SerializedName("1")
    WrongLoginOrPassword(R.string.log_in_result_code_wrong_login_or_password),
    @SerializedName("2")
    Unknown(R.string.log_in_result_code_unknown),
    @SerializedName("3")
    DefaultError(R.string.base_result_code_default_error_message);
}