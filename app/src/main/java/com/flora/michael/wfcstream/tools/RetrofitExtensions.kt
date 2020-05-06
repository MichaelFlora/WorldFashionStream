package com.flora.michael.wfcstream.tools

import android.util.Log
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

fun String.toRequestBody(): RequestBody = RequestBody.create(MultipartBody.FORM, this)

fun RequestBody.addExtraFieldToApplicationJsonRequestBody(
    fieldName: String,
    fieldValue: String
): RequestBody? {
    bodyToString(this)?.let{ customRequest ->
        try {
            val jsonObject = JSONObject(customRequest)
            jsonObject.put(fieldName, fieldValue)
            return RequestBody.create(this.contentType(), jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    return null
}

fun RequestBody.addExtraFieldToFormDataRequestBody(
    fieldName: String,
    fieldValue: String
): RequestBody? {

    val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
    val formBody = this as FormBody

    bodyBuilder.addFormDataPart(fieldName, fieldValue)

    for (i in 0 until formBody.size()) {
        bodyBuilder.addFormDataPart(formBody.encodedName(i), formBody.encodedValue(i))
    }

    return bodyBuilder.build()
}

private fun bodyToString(request: RequestBody): String? {
    return try {
        val buffer = Buffer()
        request.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}