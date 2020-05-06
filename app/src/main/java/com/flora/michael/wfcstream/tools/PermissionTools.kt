package com.flora.michael.wfcstream.tools

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener

fun checkPermission(activity: Activity, permission: String): Boolean{
    if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
        return true
    }

    return false
}

fun checkPermissions(activity: Activity, permissions: List<String>): Boolean{
    var result = true

    for(permission in permissions){
        if(!checkPermission(activity, permission)){
            result = false
            break
        }
    }

    return result
}

fun requestPermission(activity: Activity, permission: String, deniedMessage: String? = null, onDenied: ((permissionName: String?) -> Unit)? = null, onGranted: ((permissionName: String?) -> Unit)? = null) {

    val listeners: List<PermissionListener> = mutableListOf(
        getDeniedPermissionListener(onDenied),
        getSnackbarDeniedPermissionListener(activity, deniedMessage),
        getGrantedPermissionListener(onGranted)
    ).filterNotNull()

    Dexter.withActivity(activity)
        .withPermission(permission)
        .withListener(
            CompositePermissionListener(listeners)
        )
        .check()
}

fun requestPermissions(activity: Activity, permissions: List<String>, deniedMessage: String? = null, onDenied: ((permissionName: String?) -> Unit)? = null, onGranted: (() -> Unit)? = null){
    for(permission in permissions){
        requestPermission(
            activity,
            permission,
            deniedMessage,
            onDenied = {
                onDenied?.invoke(it)
                return@requestPermission
            }
        )
    }

    onGranted?.invoke()
}

private fun getDeniedPermissionListener(onDenied: ((permissionName: String?) -> Unit)? = null): PermissionListener?{
    return onDenied?.let{ onDeniedNotNull ->
        object : BasePermissionListener() {
            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                onDeniedNotNull(response?.permissionName)
            }
        }
    }
}

private fun getSnackbarDeniedPermissionListener(activity: Activity, deniedMessage: String? = null): PermissionListener?{
    return deniedMessage?.let { message ->
        SnackbarOnDeniedPermissionListener.Builder
            .with(activity.findViewById<View>(android.R.id.content), message)
            .withOpenSettingsButton("Настройки")
            .build()
    }
}

private fun getGrantedPermissionListener(onGranted: ((permissionName: String?) -> Unit)? = null): PermissionListener?{
    return onGranted?.let{ onGrantedNotNull ->
        object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                onGrantedNotNull(response?.permissionName)
            }
        }
    }
}