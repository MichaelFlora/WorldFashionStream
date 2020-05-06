package com.flora.michael.wfcstream.repository

import android.content.Context
import androidx.core.content.edit

class PreferencesRepository(context: Context){

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun setAuthorizationToken(token: String?){
        sharedPreferences.edit {
            putString(AUTHORIZATION_TOKEN_KEY, token)
        }
    }

    fun getCurrentAuthorizationToken(): String?{
        return sharedPreferences.getString(AUTHORIZATION_TOKEN_KEY, null);
    }

    companion object{
        private const val PREFERENCES_FILE_NAME = "WFCStreamPreferences"
        private const val AUTHORIZATION_TOKEN_KEY = "AuthorizationToken"
    }
}