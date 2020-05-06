package com.flora.michael.wfcstream.network

import com.flora.michael.wfcstream.tools.addExtraFieldToApplicationJsonRequestBody
import com.flora.michael.wfcstream.tools.addExtraFieldToFormDataRequestBody
import okhttp3.Interceptor
import okhttp3.Response

class RequestBodyAppendFieldInterceptor(
    private val fieldName: String,
    private val fieldValueRef: () -> String?
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.method() != "POST")
            return chain.proceed(request)

        var requestBody = request.body()
        val contentSubtype = requestBody?.contentType()?.subtype()

        fieldValueRef()?.let{ fieldValue ->
            if(contentSubtype?.contains("json") == true){
                requestBody = requestBody?.addExtraFieldToApplicationJsonRequestBody(fieldName, fieldValue)
            }else if(contentSubtype?.contains("form") == true){
                requestBody = requestBody?.addExtraFieldToFormDataRequestBody(fieldName, fieldValue);
            }

            requestBody?.let { requestBodyNotNull ->
                request = request.newBuilder()
                    .post(requestBodyNotNull)
                    .build()
            }
        }

        return chain.proceed(request)
    }
}