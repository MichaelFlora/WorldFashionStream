package com.flora.michael.wfcstream.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.flashphoner.fpwcsapi.Flashphoner
import com.flora.michael.wfcstream.NavigationGraphDirections
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.viewmodel.main.SharedViewModel

class MainActivity : AppCompatActivity(R.layout.main_activity) {
    private val viewModel by viewModels<SharedViewModel>()
    private val navigationController by lazy { findNavController(R.id.navigation_host_fragment) }

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findAllViews()
        initializeToolbar()
        initializeFlashphoner()
        startTokenObservation()
    }

    private fun findAllViews(){
        toolbar = findViewById(R.id.toolbar)
    }

    private fun startTokenObservation(){
        viewModel.authorizationToken.observe(this, Observer{ token ->
            if(token == null){
                val action = NavigationGraphDirections.actionGlobalDestinationWelcome()
                navigationController.navigate(action)
            }
        })
    }

    private fun initializeToolbar(){
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.destination_home, R.id.destination_welcome).build()
        setupActionBarWithNavController(navigationController, appBarConfiguration)
    }

    private fun initializeFlashphoner(){
        Flashphoner.init(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp()
    }

}
