package com.flora.michael.wfcstream.ui.welcome

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.ui.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.welcome.WelcomeViewModel
import com.google.android.material.button.MaterialButton

class WelcomeFragment: LoadableContentFragment(R.layout.welcome_fragment) {
    private val viewModel by viewModels<WelcomeViewModel>()

    private var welcomeMessageTextView: TextView? = null
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
            welcomeMessageTextView = findViewById(R.id.welcome_fragment_message)
            logInButton = findViewById(R.id.welcome_fragment_log_in_button)
            signInButton = findViewById(R.id.welcome_fragment_sign_in_button)
        }
    }

    private fun initializeAllViews(){
        initializeWelcomeMessageTextView()
        initializeLogInButton()
        initializeSignInButton()
    }

    private fun initializeWelcomeMessageTextView(){
        welcomeMessageTextView?.text = getString(R.string.welcome_fragment_welcome_message, getString(R.string.app_name))
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