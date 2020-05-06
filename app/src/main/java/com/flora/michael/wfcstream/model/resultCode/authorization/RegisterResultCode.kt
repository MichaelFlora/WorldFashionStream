package com.flora.michael.wfcstream.model.resultCode.authorization

import androidx.annotation.StringRes
import com.flora.michael.wfcstream.R
import com.google.gson.annotations.SerializedName

enum class RegisterResultCode(
    @StringRes
    val description: Int
){
    @SerializedName("0")
    Success(R.string.register_result_code_success),
    @SerializedName("1")
    LoginAlreadyExists(R.string.register_result_code_login_already_exists),
    @SerializedName("2")
    UserNameIsAlreadyUsed(R.string.register_result_code_user_name_is_already_used),
    @SerializedName("3")
    Unknown(R.string.register_result_code_unknown),
    @SerializedName("4")
    DefaultError(R.string.base_result_code_default_error_message);
}