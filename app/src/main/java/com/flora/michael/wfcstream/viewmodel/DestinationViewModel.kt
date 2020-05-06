package com.flora.michael.wfcstream.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

abstract class DestinationViewModel(application: Application): AndroidViewModel(application), KodeinAware {
    override val kodein by closestKodein()

    private val mutableIsContentLoading = MutableLiveData<Boolean>()
    private var loadingOperationsCount: Int = 0

    val isContentLoading: LiveData<Boolean> = mutableIsContentLoading

    protected fun setLoadingOperationStarted(){
        if(loadingOperationsCount <= 0)
            mutableIsContentLoading.value = true

        loadingOperationsCount++
    }

    protected fun setLoadingOperationFinished(){
        if(loadingOperationsCount > 0)
            loadingOperationsCount--

        if(loadingOperationsCount <= 0)
            mutableIsContentLoading.value = false
    }
}