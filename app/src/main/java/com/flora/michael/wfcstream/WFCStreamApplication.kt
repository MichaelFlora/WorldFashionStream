package com.flora.michael.wfcstream

import android.app.Application
import android.content.Context
import com.flora.michael.wfcstream.network.BackendApiManager
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.BroadcastsRepository
import com.flora.michael.wfcstream.repository.PreferencesRepository
import com.flora.michael.wfcstream.repository.SessionRepository
import com.flora.michael.wfcstream.repository.wfc_stream_api.AuthorizationApi
import com.flora.michael.wfcstream.repository.wfc_stream_api.BroadcastsApi
import com.flora.michael.wfcstream.repository.wfc_stream_api.SessionApi
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class WFCStreamApplication : Application(), KodeinAware{
    override val kodein: Kodein = Kodein.lazy {
        bind<Context>() with singleton{ applicationContext }
        bind<PreferencesRepository>() with singleton { PreferencesRepository(instance()) }
        //bind<RetrofitManagerOld>() with singleton { RetrofitManagerOld(instance()) }
//        bind<Retrofit>() with singleton {
//            val retrofitManager = instance<RetrofitManagerOld>()
//
//            retrofitManager.createRetrofit(
//                retrofitManager.getDefaultOkHttpClient(),
//                "http://streaming2020.mywfc.ru/api/",
//                null
//            )
//        }
        bind<BackendApiManager>() with singleton {
            BackendApiManager.builder(
                "http://streaming2020.mywfc.ru/api/",
                kodein)
                .build()
        }
        bind<SessionApi>() with singleton{ instance<BackendApiManager>().provideApi(SessionApi::class.java) }
        bind<AuthorizationApi>() with singleton{ instance<BackendApiManager>().provideApi(AuthorizationApi::class.java, secureSession = true) }
        bind<BroadcastsApi>() with singleton{ instance<BackendApiManager>().provideApi(BroadcastsApi::class.java, secureSession = true) }
        bind<SessionRepository>() with singleton{ SessionRepository(instance()) }
        bind<AuthorizationRepository>() with singleton{ AuthorizationRepository(instance(), instance()) }
        bind<BroadcastsRepository>() with singleton { BroadcastsRepository(instance()) }
    }
}