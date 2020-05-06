package com.flora.michael.wfcstream.viewmodel.main

import android.app.Application
import androidx.lifecycle.*
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import com.flora.michael.wfcstream.repository.PreferencesRepository
import com.flora.michael.wfcstream.repository.SessionRepository
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SharedViewModel(application: Application): AndroidViewModel(application), KodeinAware {
    override val kodein by closestKodein()

    private val sessionRepository: SessionRepository by instance()
    private val authorizationRepository: AuthorizationRepository by instance()

    val authorizationToken: LiveData<String> = authorizationRepository.currentAccessToken


    init {
        viewModelScope.launch {
            sessionRepository.updateSessionToken()
        }
    }


}