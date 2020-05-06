package com.flora.michael.wfcstream.network

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.flora.michael.wfcstream.BuildConfig
import com.flora.michael.wfcstream.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.HashSet
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RetrofitManagerOld(private val applicationContext: Context) {
    private var cookies = HashSet<String>()

    private val certificatesFiles = listOf<@RawRes Int>(
//        R.raw.dixy_certificate_1,
//        R.raw.dixy_certificate_2,
//        R.raw.dixy_certificate_3
    )

    private val addCookiesInterceptor: Interceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
            Log.v("OkHttp", "Adding Header: $cookie")
        }

        chain.proceed(builder.build())
    }

    private val receivedCookiesInterceptor: Interceptor = Interceptor { chain ->

        val originalResponse = chain.proceed(chain.request())

        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            cookies = HashSet<String>(originalResponse.headers("Set-Cookie"))
        }

        originalResponse
    }

    private val loggingInterceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                interceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            return interceptor
        }

    private val encodingInterceptor: Interceptor
        get() = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build()
        }

    private val acceptInterceptor: Interceptor = Interceptor { chain ->
        val original = chain.request()
        val withTokenRequestBuilder = original.newBuilder().addHeader("Accept", "application/json")

        chain.proceed(withTokenRequestBuilder.build())
    }

    private val errorLoggingInterceptor: Interceptor = Interceptor { chain ->
        val response: Response? = chain.proceed(chain.request())
        val body = response?.body()
        val code = response?.code()

        body?.let { bodyNotNull ->
            val bodyString = bodyNotNull.string()

            val contentType = bodyNotNull.contentType()

            var responseBuilder = response.newBuilder().body(ResponseBody.create(contentType, bodyString))

            code?.let { codeNotNull ->
                try {
                    if (codeNotNull.toString().startsWith("4") || codeNotNull.toString().startsWith("5")) {
                        //Crashlytics.logException(Exception(bodyString))
                    }
                } catch (e: Exception) {

                }

                responseBuilder = responseBuilder.code(code)
            }

            responseBuilder.build()
        }
    }

    fun createRetrofit(client: OkHttpClient, baseUrl: String, safeConnection: Boolean? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(
                safeConnection?.let{
                    if (it) getSafeOkHTTPClient(client) else getUnsafeOkHTTPClient(client)
                } ?: client
            )
            .build()
    }

    fun getDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .readTimeout(65000, TimeUnit.MILLISECONDS)
            .connectTimeout(65000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            //.addInterceptor(encodingInterceptor)
            .addInterceptor(addCookiesInterceptor)
            .addInterceptor(receivedCookiesInterceptor)
            .build()
    }

    fun getSafeOkHTTPClient(client: OkHttpClient, context: Context = applicationContext): OkHttpClient {
        return client.newBuilder().apply {
            generateSSLContext(context)?.let {
                sslSocketFactory(it.socketFactory)
            }
        }.build()
    }

    fun getUnsafeOkHTTPClient(safe: OkHttpClient): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager> (
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers () : Array<X509Certificate> {
                        return emptyArray()
                    }

                })

            val sslContext = SSLContext.getInstance("SSLv3")
            sslContext.init(null, trustAllCerts, SecureRandom ())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = safe.newBuilder()
            builder.sslSocketFactory(sslSocketFactory,  trustAllCerts [0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }

            return builder.build()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return safe
    }

    private fun createGson(): Gson = GsonBuilder().setDateFormat("dd.MM.yyyy").create()

    private fun generateSSLContext(context: Context): SSLContext? {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        var certificateStream: BufferedInputStream? = null

        try {

            val certificates = mutableListOf<Certificate>()

            for(certificateFile in certificatesFiles){
                certificateStream?.close()
                certificateStream = BufferedInputStream(context.resources.openRawResource(certificateFile))

                val certificate = certificateFactory.generateCertificate(certificateStream)

                certificates.add(certificate)
            }

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)

                for(index in certificates.indices){
                    setCertificateEntry("index", certificates[index])
                }
            }

            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

            return SSLContext.getInstance("TLSv1").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            certificateStream?.close()
        }

        return null
    }

}