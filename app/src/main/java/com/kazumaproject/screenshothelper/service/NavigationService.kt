package com.kazumaproject.screenshothelper.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class NavigationService: AccessibilityService() {

    lateinit var navigationService : NavigationService

    fun isInitialize(): Boolean{
        return this::navigationService.isInitialized
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onServiceConnected() {
        super.onServiceConnected()
        navigationService = this
    }
}