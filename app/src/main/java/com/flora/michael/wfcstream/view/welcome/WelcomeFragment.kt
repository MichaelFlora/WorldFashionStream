package com.flora.michael.wfcstream.view.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.welcome.WelcomeViewModel
import com.google.android.material.button.MaterialButton

class WelcomeFragment: LoadableContentFragment(R.layout.welcome_fragment) {
    private val viewModel by viewModels<WelcomeViewModel>()

    private var logInButton: MaterialButton? = null
    private var signInButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun findAllViews(){
        view?.apply {
            logInButton = findViewById(R.id.welcome_log_in_button)
            signInButton = findViewById(R.id.welcome_sign_in_button)
        }
    }

    private fun initializeAllViews(){
        initializeLogInButton()
        initializeSignInButton()
    }

    private fun initializeLogInButton(){
        val action = WelcomeFragmentDirections.actionDestinationWelcomeToDestinationLogIn()

        logInButton?.setOnClickListener {
            navigationController.navigate(action)
        }
    }

    private fun initializeSignInButton(){
        val action = WelcomeFragmentDirections.actionDestinationWelcomeToDestinationRegistration()

        signInButton?.setOnClickListener {
            navigationController.navigate(action)
        }
    }
}