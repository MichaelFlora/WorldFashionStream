package com.flora.michael.wfcstream.viewmodel.stream

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class StreamViewModel(application: Application): AndroidViewModel(application){
    var broadcastId: Long = -1
        private set

    fun initialize(broadcastId: Long){
        this.broadcastId = broadcastId
    }
}