package com.flora.michael.wfcstream.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.flashphoner.fpwcsapi.Flashphoner
import com.flora.michael.wfcstream.NavigationGraphDirections
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.viewmodel.home.HomeViewModel
import com.flora.michael.wfcstream.viewmodel.main.SharedViewModel
import org.kodein.di.KodeinAware

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
