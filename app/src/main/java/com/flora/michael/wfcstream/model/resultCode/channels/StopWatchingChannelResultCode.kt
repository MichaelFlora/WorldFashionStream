package com.flora.michael.wfcstream.model.resultCode.channels

import androidx.annotation.StringRes
import com.flora.michael.wfcstream.R
import com.google.gson.annotations.SerializedName

enum class StopWatchingChannelResultCode(
    @StringRes
    val description: Int
){
    @SerializedName("0")
    Success(R.string.stop_watching_channel_result_code_success),
    @SerializedName("1")
    TokenDoesNotExist(R.string.stop_watching_channel_result_code_token_does_not_exist),
    @SerializedName("2")
    ChannelDoesNotExist(R.string.stop_watching_channel_result_code_channel_does_not_exist),
    @SerializedName("3")
    ChannelIsOffline(R.string.stop_watching_channel_result_code_channel_is_offline),
    @SerializedName("4")
    UserIsNotWatchingThisChannel(R.string.stop_watching_channel_result_code_user_is_not_watching_the_broadcast),
    @SerializedName("5")
    Unknown(R.string.stop_watching_channel_result_code_unknown),
    @SerializedName("6")
    DefaultError(R.string.base_result_code_default_error_message);
}