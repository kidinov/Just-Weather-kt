package org.kidinov.justweather.main

import android.app.Application
import org.kidinov.justweather.BuildConfig
import org.kidinov.justweather.main.di.weatherActivityModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin(this, listOf(weatherActivityModule))
    }
}