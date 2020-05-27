package com.flora.michael.wfcstream.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flora.michael.wfcstream.model.resultCode.authorization.LogInResultCode
import com.flora.michael.wfcstream.model.resultCode.authorization.LogOutResultCode
import com.flora.michael.wfcstream.model.resultCode.authorization.RegisterResultCode
import com.flora.michael.wfcstream.repository.wfsBroadcastApi.AuthorizationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthorizationRepository(
    private val authorizationApi: AuthorizationApi,
    private val preferencesRepository: PreferencesRepository
) {
    private val currentAccessTokenMutable = MutableLiveData<String>(preferencesRepository.getCurrentAuthorizationToken())
    val currentAccessToken: LiveData<String> = currentAccessTokenMutable

    suspend fun logIn(login: String, password: String): LogInResultCode = withContext(Dispatchers.IO){

        var resultCode: LogInResultCode = LogInResultCode.DefaultError

        try{
            val response = authorizationApi.logIn(login, password)

            if(response.isSuccessful){
                response.body()?.let { logInResponse ->
                    withContext(Dispatchers.Main){
                        if(logInResponse.resultCode == LogInResultCode.Success){
                            setAccessToken(logInResponse.accessToken)
                        }
                    }

                    resultCode = logInResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun register(
        login: String,
        password: String,
        userName: String,
        email: String? = null
    ): RegisterResultCode = withContext(Dispatchers.IO){

        var resultCode: RegisterResultCode = RegisterResultCode.DefaultError

        try{
            val response = authorizationApi.register(login, password, userName, email)

            if(response.isSuccessful){
                response.body()?.let { registerResponse ->
                    resultCode = registerResponse.resultCode
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    suspend fun logOut(): LogOutResultCode = withContext(Dispatchers.IO){

        var resultCode: LogOutResultCode = LogOutResultCode.DefaultError

        try{
            currentAccessToken.value?.let { accessToken ->
                val response = authorizationApi.logOut(accessToken)

                withContext(Dispatchers.Main) {
                    clearAccessToken()
                }

                if(response.isSuccessful){
                    response.body()?.let { registerResponse ->
                        resultCode = registerResponse.resultCode
                    }
                }
            }
        } catch(ex: HttpException){
            ex.printStackTrace()
        }

        resultCode
    }

    private fun clearAccessToken(){
        preferencesRepository.setAuthorizationToken(null)
        currentAccessTokenMutable.value = null
    }

    private fun setAccessToken(token: String){
        preferencesRepository.setAuthorizationToken(token)
        currentAccessTokenMutable.value = token
    }
}