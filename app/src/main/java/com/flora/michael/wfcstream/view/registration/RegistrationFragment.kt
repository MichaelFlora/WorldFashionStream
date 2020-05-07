package com.flora.michael.wfcstream.view.registration

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.model.resultCode.authorization.RegisterResultCode
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.view.login.LogInFragmentDirections
import com.flora.michael.wfcstream.viewmodel.registration.RegistrationViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistrationFragment : LoadableContentFragment(R.layout.registration_fragment) {
    private val viewModel by viewModels<RegistrationViewModel>()

    private var loginTextInputLayout: TextInputLayout? = null
    private var loginEditText: TextInputEditText? = null
    private var userNameTextInputLayout: TextInputLayout? = null
    private var userNameEditText: TextInputEditText? = null
    private var passwordTextInputLayout: TextInputLayout? = null
    private var passwordEditText: TextInputEditText? = null
    private var confirmPasswordTextInputLayout: TextInputLayout? = null
    private var confirmPasswordEditText: TextInputEditText? = null
    private var registerButton: MaterialButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        startObservingSuccessfulRegistration()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun findAllViews(){
        view?.run{
            loginTextInputLayout = findViewById(R.id.registration_fragment_login_input_layout)
            loginEditText = findViewById(R.id.registration_fragment_login_edit_text)
            userNameTextInputLayout = findViewById(R.id.registration_fragment_user_name_input_layout)
            userNameEditText = findViewById(R.id.registration_fragment_user_name_edit_text)
            passwordTextInputLayout = findViewById(R.id.registration_fragment_password_input_layout)
            passwordEditText = findViewById(R.id.registration_fragment_password_edit_text)
            confirmPasswordTextInputLayout = findViewById(R.id.registration_fragment_confirm_password_input_layout)
            confirmPasswordEditText = findViewById(R.id.registration_fragment_confirm_password_edit_text)
            registerButton = findViewById(R.id.registration_fragment_register_button)
        }
    }

    private fun initializeAllViews(){
        initializeLoginEditText()
        initializeUserNameEditText()
        initializePasswordEditText()
        initializeConfirmPasswordEditText()
        initializeRegistrationButton()
        initializeRegistrationResultToast()
    }

    private fun initializeLoginEditText(){
        loginTextInputLayout?.apply {
            isCounterEnabled = true
            counterMaxLength = RegistrationViewModel.MAX_LOGIN_LENGTH
            isErrorEnabled = true

            viewModel.loginError.observe(viewLifecycleOwner, Observer { error ->
                this.error = error
            })
        }

        loginEditText?.apply{
            filters = arrayOf(InputFilter.LengthFilter(RegistrationViewModel.MAX_LOGIN_LENGTH))

            addTextChangedListener { editable: Editable? ->
                editable?.toString()?.let { enteredLogin ->
                    viewModel.updateLogin(enteredLogin)
                }
            }
        }
    }

    private fun initializeUserNameEditText(){
        userNameTextInputLayout?.apply {
            isCounterEnabled = true
            counterMaxLength = RegistrationViewModel.MAX_USER_NAME_LENGTH
            isErrorEnabled = true

            viewModel.userNameError.observe(viewLifecycleOwner, Observer { error ->
                this.error = error
            })
        }

        userNameEditText?.apply {
            filters = arrayOf(InputFilter.LengthFilter(RegistrationViewModel.MAX_USER_NAME_LENGTH))

            addTextChangedListener { editable: Editable? ->
                editable?.toString()?.let { enteredUserName ->
                    viewModel.updateUserName(enteredUserName)
                }
            }
        }
    }

    private fun initializePasswordEditText(){
        passwordTextInputLayout?.apply {
            isCounterEnabled = true
            counterMaxLength = RegistrationViewModel.MAX_PASSWORD_LENGTH
            isErrorEnabled = true

            viewModel.passwordError.observe(viewLifecycleOwner, Observer { error ->
                this.error = error
            })
        }

        passwordEditText?.apply {
            filters = arrayOf(InputFilter.LengthFilter(RegistrationViewModel.MAX_PASSWORD_LENGTH))

            addTextChangedListener { editable: Editable? ->
                editable?.toString()?.let { enteredPassword ->
                    viewModel.updatePassword(enteredPassword)
                }
            }
        }
    }

    private fun initializeConfirmPasswordEditText(){
        confirmPasswordTextInputLayout?.apply {
            isErrorEnabled = true

            viewModel.confirmPasswordError.observe(viewLifecycleOwner, Observer { error ->
                this.error = error
            })
        }

        confirmPasswordEditText?.apply {
            addTextChangedListener { editable: Editable? ->
                editable?.toString()?.let { enteredConfirmPassword ->
                    viewModel.updateConfirmPassword(enteredConfirmPassword)
                }
            }
        }
    }

    private fun initializeRegistrationButton(){
        viewModel.isEnteredDataErrorsExist.observe(viewLifecycleOwner, Observer{ isErrorExist: Boolean? ->
            registerButton?.isEnabled = isErrorExist?.not() ?: false
        })

        registerButton?.setOnClickListener {
            viewModel.register()
            showLoadingProgressBar()
        }
    }

    private fun initializeRegistrationResultToast(){
        viewModel.registrationResult.observe(viewLifecycleOwner, Observer{ registrationResult ->
            hideLoadingProgressBar()

            registrationResult?.let { errorDescription ->
                Toast.makeText(context, errorDescription, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startObservingSuccessfulRegistration(){
        sharedViewModel.authorizationToken.observe(viewLifecycleOwner, Observer { accessToken ->
            if(accessToken != null){
                val action = RegistrationFragmentDirections.actionDestinationRegistrationToDestinationHome()
                navigationController.navigate(action)
            }
        })
    }
}