package com.flora.michael.wfcstream.tools

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
    val bodyBuilder = FormBody.Builder()
    val formBody = this as FormBody

    bodyBuilder.add(fieldName, fieldValue)

    for (i in 0 until formBody.size()) {
        bodyBuilder.add(formBody.encodedName(i), formBody.encodedValue(i))
    }

    return bodyBuilder.build()
}

fun RequestBody.addExtraFieldToMultipartRequestBody(
    fieldName: String,
    fieldValue: String
): RequestBody? {
    val formBody = this as MultipartBody
    val bodyBuilder = MultipartBody.Builder().setType(formBody.type())

    bodyBuilder.addFormDataPart(fieldName, fieldValue)

    for (i in 0 until formBody.size()) {
        bodyBuilder.addPart(formBody.part(i))
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