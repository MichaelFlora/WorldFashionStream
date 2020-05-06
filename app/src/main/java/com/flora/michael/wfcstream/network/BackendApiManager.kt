package com.flora.michael.wfcstream.network

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.flora.michael.wfcstream.BuildConfig
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.PreferencesRepository
import com.flora.michael.wfcstream.repository.SessionRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.HashSet
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class BackendApiManager private constructor(private val baseURL: String, override val kodein: Kodein): KodeinAware {
    private val logTag = this::class.java.simpleName
    private var applicationContext: Context? = null
    private val preferencesRepository: PreferencesRepository by instance()
    private val sessionRepository: SessionRepository by instance()
    private val authorizationRepository: AuthorizationRepository by instance()

    private var cookies = HashSet<String>()
    private var allCertificatesAreTrusted: Boolean = false
    private val certificateFilesIds = mutableListOf<@RawRes Int>()

    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val sessionOutdatedInterceptor: Interceptor = Interceptor { chain ->
        var response = chain.proceed(chain.request())
        val request = chain.request()

//        if(!response.isSuccessful && response.code() == 419){
//            GlobalScope.launch {
//                sessionRepository.updateSessionToken()
//            }
//        }

        var currentAttempt = 0

        while(!response.isSuccessful && response.code() == 419 && currentAttempt < RETRY_ATTEMPTS_COUNT){
            currentAttempt++

            runBlocking {
                sessionRepository.updateSessionToken()
                Log.d(logTag, "Updated session token")
            }

            Log.d(logTag, "Retrying last request")

            response = chain.proceed(request)
        }

        response
    }

    private val receivedCookiesInterceptor: Interceptor = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())

        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            cookies = HashSet<String>(originalResponse.headers("Set-Cookie"))
        }

        originalResponse
    }

    private val cookiesAppendInterceptor: Interceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
            Log.v(logTag, "Adding Header: $cookie")
        }

        chain.proceed(builder.build())
    }

    private val acceptInterceptor: Interceptor = Interceptor { chain ->
        val original = chain.request()
        val withTokenRequestBuilder = original.newBuilder().addHeader("Accept", "application/json")

        chain.proceed(withTokenRequestBuilder.build())
    }

    private val secureSessionPostInterceptor = RequestBodyAppendFieldInterceptor("_token"){ sessionRepository.currentSessionToken }
    private val authorizedPostInterceptor = RequestBodyAppendFieldInterceptor("access_token"){ preferencesRepository.getCurrentAuthorizationToken() }

    private var defaultOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
        .readTimeout(READ_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(receivedCookiesInterceptor)
        .addInterceptor(cookiesAppendInterceptor)
        .addInterceptor(acceptInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
        //.initializeWithBuilderData()

    private var secureSessionOkHttpClient: OkHttpClient = defaultOkHttpClient.newBuilder()
        .apply{ interceptors().add(0, sessionOutdatedInterceptor) }
        .addInterceptor(secureSessionPostInterceptor)

        .build()
        //.initializeWithBuilderData()

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(defaultOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val secureSessionRetrofit = retrofit.newBuilder()
        .client(secureSessionOkHttpClient)
        .build()

    fun <Api> provideApi(apiClass: Class<Api>, secureSession: Boolean = false): Api{
        return if(secureSession){
            secureSessionRetrofit.create(apiClass)
        } else{
            retrofit.create(apiClass)
        }
    }

    private fun OkHttpClient.initializeWithBuilderData(): OkHttpClient{
        if(baseURL.startsWith("https:")){
            return if(allCertificatesAreTrusted){
                toNotSecureClient()
            } else{
                applicationContext?.let{
                    initializeWithTrustedCAs(it)
                } ?: this
            }
        }

        return this
    }

    private fun OkHttpClient.initializeWithTrustedCAs(context: Context): OkHttpClient{
        return newBuilder().apply {
            generateSSLContextWithTrustedCAs(context)?.let {
                sslSocketFactory(it.socketFactory)
            }
        }.build()
    }

    private fun OkHttpClient.toNotSecureClient(): OkHttpClient{
        try {
            // Create a trust manager that does not validate certificate chains
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

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom ())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = newBuilder()
            builder.sslSocketFactory(sslSocketFactory,  trustAllCerts [0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }

            return builder.build()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }

    private fun generateSSLContextWithTrustedCAs(context: Context): SSLContext? {

        val certificates = getCertificates(context)
        val protocol = "TLSv1"

        getKeyStoreWithTrustedCertificateAuthorities(certificates)?.let { keyStore ->
            getTrustManagerFactoryWithCAsKeyStore(keyStore)?.let { trustManagerFactory ->
                try{
                    return SSLContext.getInstance(protocol).apply {
                        init(null, trustManagerFactory.trustManagers, null)
                    }
                } catch (noSuchAlgorithmException: NoSuchAlgorithmException){
                    Log.e(logTag, "No one provider supports a TrustManagerFactorySpi implementation for protocol $protocol", noSuchAlgorithmException)
                }
            }
        }

        return null
    }

    private fun getCertificates(context: Context): List<X509Certificate> {
        val certificateFactory = CertificateFactory.getInstance("X.509")

        val certificates = mutableListOf<X509Certificate>()

        certificateFilesIds.forEachIndexed { index, certificateFileId ->
            var certificateStream: BufferedInputStream? = null

            try {
                certificateStream = BufferedInputStream(context.resources.openRawResource(certificateFileId))

                (certificateFactory.generateCertificate(certificateStream) as? X509Certificate)?.let { certificate ->
                    certificates.add(certificate)
                } ?: Log.e(logTag, "Invalid certificate with index $index")
            } catch (ioException: IOException) {
                Log.e(logTag, "Error occurred while operating certificate file", ioException)
            } catch (certificateException: CertificateException) {
                Log.e(logTag, "Error occurred while generating certificate", certificateException)
            } finally {
                certificateStream?.close()
            }
        }

        return certificates
    }

    private fun getKeyStoreWithTrustedCertificateAuthorities(certificates: List<X509Certificate>): KeyStore?{
        try{
            val keyStoreType = KeyStore.getDefaultType()

            return KeyStore.getInstance(keyStoreType).apply {
                load(null, null)

                certificates.forEachIndexed{ index, certificate ->
                    setCertificateEntry("dixyCertificate$index", certificate)
                }
            }
        } catch (keyStoreException: KeyStoreException) {
            Log.e(logTag, "No one provider supports a KeyStoreSpi implementation for the default type", keyStoreException)
        }

        return null
    }

    private fun getTrustManagerFactoryWithCAsKeyStore(keyStore: KeyStore): TrustManagerFactory?{
        val trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm()

        try{
            return TrustManagerFactory.getInstance(trustManagerAlgorithm).apply {
                init(keyStore)
            }
        } catch (noSuchAlgorithmException: NoSuchAlgorithmException){
            Log.e(logTag, "Failed to get TrustManagerFactory because of unknown algorithm", noSuchAlgorithmException)
        }

        return null
    }

    inner class Builder(){

        fun setTrustedCertificates(applicationContext: Context, @RawRes vararg certificateFilesIds: Int): Builder{
            this@BackendApiManager.applicationContext = applicationContext
            this@BackendApiManager.certificateFilesIds.clear()
            this@BackendApiManager.certificateFilesIds.addAll(certificateFilesIds.toList())

            return this
        }

        fun trustAllCertificates(allCertificatesAreTrusted: Boolean): Builder{
            this@BackendApiManager.allCertificatesAreTrusted = allCertificatesAreTrusted
            return this
        }

        fun build(): BackendApiManager = this@BackendApiManager
    }

    companion object{
        private const val RETRY_ATTEMPTS_COUNT = 10
        private const val API_BASE_URL = "http://streaming2020.mywfc.ru/api/"
        private const val READ_TIMEOUT_MILLISECONDS = 60000L
        private const val CONNECTION_TIMEOUT_MILLISECONDS = 60000L

        fun builder(baseURL: String, kodein: Kodein): Builder = BackendApiManager(baseURL, kodein).Builder()
    }
}