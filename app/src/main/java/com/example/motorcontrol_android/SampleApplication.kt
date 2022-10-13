package com.example.motorcontrol_android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.sagebionetworks.motorControlModule

class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin{
            androidLogger(Level.ERROR)
            androidContext(this@SampleApplication)
            modules(appModule)
            modules(motorControlModule)
        }
    }
}