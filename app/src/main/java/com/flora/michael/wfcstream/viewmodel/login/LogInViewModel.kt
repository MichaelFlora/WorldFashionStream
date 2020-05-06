package com.flora.michael.wfcstream.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flora.michael.wfcstream.model.resultCode.authorization.LogInResultCode
import com.flora.michael.wfcstream.repository.AuthorizationRepository
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LogInViewModel(application: Application): AndroidViewModel(application), KodeinAware {
    override val kodein by closestKodein()
    private val authorizationRepository: AuthorizationRepository by instance()
    private val userNameMutable = MutableLiveData<String>()
    private val passwordMutable = MutableLiveData<String>()
    private val logInResultMutable = MutableLiveData<LogInResultCode>()

    val userName: LiveData<String> = userNameMutable
    val password: LiveData<String> = passwordMutable
    val logInResult: LiveData<LogInResultCode> = logInResultMutable

    fun updateUserName(newUserName: String){
        userNameMutable.value = newUserName
    }

    fun updatePassword(newPassword: String){
        passwordMutable.value = newPassword
    }

    fun logIn(){
        userNameMutable.value?.let{ userName ->
            passwordMutable.value?.let { password ->
                viewModelScope.launch {
                    logInResultMutable.value = authorizationRepository.logIn(userName, password)
                }
            }
        }
    }
}