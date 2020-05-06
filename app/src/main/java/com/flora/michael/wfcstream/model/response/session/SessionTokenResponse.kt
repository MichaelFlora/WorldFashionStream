package com.flora.michael.wfcstream.model.response.session

import com.google.gson.annotations.SerializedName

data class SessionTokenResponse (
    @SerializedName("csrf")
    val sessionToken: String
)