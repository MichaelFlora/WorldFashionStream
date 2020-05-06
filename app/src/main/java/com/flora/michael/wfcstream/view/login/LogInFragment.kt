package com.flora.michael.wfcstream.view.login

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.viewmodel.login.LogInViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LogInFragment : LoadableContentFragment(R.layout.log_in_fragment) {
    private val viewModel by viewModels<LogInViewModel>()

    private var userNameEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var logInButton: MaterialButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        startObservingSuccessfulLogIn()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun findAllViews(){
        view?.apply {
            userNameEditText = findViewById(R.id.log_in_fragment_user_name_edit_text)
            passwordEditText = findViewById(R.id.log_in_fragment_password_edit_text)
            logInButton = findViewById(R.id.log_in_fragment_log_in_button)
        }
    }

    private fun initializeAllViews(){
        initializeUserNameEditText()
        initializePasswordEditText()
        initializeLogInButton()
        initializeLogInResultToast()
    }

    private fun initializeUserNameEditText(){
        userNameEditText?.addTextChangedListener { editable: Editable? ->
            editable?.toString()?.let { enteredUserName ->
                viewModel.updateUserName(enteredUserName)
            }
        }
    }

    private fun initializePasswordEditText(){
        passwordEditText?.addTextChangedListener { editable: Editable? ->
            editable?.toString()?.let { enteredPassword ->
                viewModel.updatePassword(enteredPassword)
            }
        }
    }

    private fun initializeLogInButton(){
        logInButton?.setOnClickListener {
            viewModel.logIn()
        }
    }

    private fun initializeLogInResultToast(){
        viewModel.logInResult.observe(viewLifecycleOwner, Observer{ logInResult ->
            logInResult?.let {
                Toast.makeText(context, it.description, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startObservingSuccessfulLogIn(){
        sharedViewModel.authorizationToken.observe(viewLifecycleOwner, Observer { accessToken ->
            if(accessToken != null){
                val action = LogInFragmentDirections.actionDestinationLogInToDestinationHome()
                navigationController.navigate(action)
                //navigationController.popBackStack(R.id.destination_home, false)
            }
        })
    }
}