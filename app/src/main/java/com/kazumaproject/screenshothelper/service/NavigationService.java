package com.kazumaproject.screenshothelper.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class NavigationService extends AccessibilityService {
    public static NavigationService navigationService;

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    public void onInterrupt() {
    }


    public void onServiceConnected() {
        super.onServiceConnected();
        navigationService = this;
    }
}
