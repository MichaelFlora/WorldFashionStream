package com.flora.michael.wfcstream

import android.app.Application
import android.content.Context
import com.flora.michael.wfcstream.network.BackendApiManager
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.ChannelsRepository
import com.flora.michael.wfcstream.repository.PreferencesRepository
import com.flora.michael.wfcstream.repository.SessionRepository
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.AuthorizationApi
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.ChannelsApi
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.SessionApi
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
        bind<ChannelsApi>() with singleton{ instance<BackendApiManager>().provideApi(ChannelsApi::class.java, secureSession = true) }
        bind<SessionRepository>() with singleton{ SessionRepository(instance()) }
        bind<AuthorizationRepository>() with singleton{ AuthorizationRepository(instance(), instance()) }
        bind<ChannelsRepository>() with singleton { ChannelsRepository(instance()) }
    }
}