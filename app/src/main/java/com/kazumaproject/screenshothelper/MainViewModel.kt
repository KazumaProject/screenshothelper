package com.kazumaproject.screenshothelper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kazumaproject.screenshothelper.service.NavigationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    navigationService: NavigationService
) : ViewModel() {

    private val _drawOverOtherAppPermission: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().also { mutableLiveData ->
            mutableLiveData.value = Settings.canDrawOverlays(context)
        }

    val drawOverOtherAppPermission: LiveData<Boolean>
        get() = _drawOverOtherAppPermission

    private val _accessibilityServicePermission: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().also { mutableLiveData ->
            mutableLiveData.value = navigationService.isInitialize()
        }

    val accessibilityServicePermission: LiveData<Boolean>
        get() = _accessibilityServicePermission

    private val _bothPermissions: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().also { mutableLiveData ->
            mutableLiveData.value = Settings.canDrawOverlays(context) && navigationService.isInitialize()
        }

    val bothPermissions: LiveData<Boolean>
        get() = _bothPermissions

}