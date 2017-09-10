package org.kidinov.just_weather.main.common.injection.module

import android.app.Application
import android.content.Context

import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val context: Application) {

    @Provides
    internal fun provideContext(): Context {
        return context
    }
}
