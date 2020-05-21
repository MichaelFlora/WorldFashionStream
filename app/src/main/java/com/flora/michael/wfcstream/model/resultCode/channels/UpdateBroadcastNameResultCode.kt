package com.flora.michael.wfcstream.model.resultCode.channels

import androidx.annotation.StringRes
import com.flora.michael.wfcstream.R
import com.google.gson.annotations.SerializedName

enum class UpdateBroadcastNameResultCode(
    @StringRes
    val description: Int
) {
    @SerializedName("0")
    Success(R.string.update_broadcast_name_result_code_success),
    @SerializedName("1")
    TokenDoesNotExist(R.string.update_broadcast_name_result_code_token_does_not_exist),
    @SerializedName("2")
    Unknown(R.string.update_broadcast_name_result_code_unknown),
    @SerializedName("3")
    DefaultError(R.string.base_result_code_default_error_message);
}