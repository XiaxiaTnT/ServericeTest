package com.example.x.servicetest;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
