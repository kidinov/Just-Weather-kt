package org.kidinov.just_weather.main

import android.app.Application
import org.kidinov.just_weather.BuildConfig
import org.kidinov.just_weather.main.common.injection.component.DaggerRepositoryComponent
import org.kidinov.just_weather.main.common.injection.component.RepositoryComponent
import org.kidinov.just_weather.main.common.injection.module.ApplicationModule
import org.kidinov.just_weather.main.common.injection.module.LocalDataSourceModule
import org.kidinov.just_weather.main.common.injection.module.RemoteDataSourceModule
import timber.log.Timber

class App : Application() {
    lateinit var component: RepositoryComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        component = DaggerRepositoryComponent.builder()
                .remoteDataSourceModule(RemoteDataSourceModule())
                .localDataSourceModule(LocalDataSourceModule())
                .applicationModule(ApplicationModule(this))
                .build()
        component.inject(this)
    }

}