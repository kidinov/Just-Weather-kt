package org.kidinov.just_weather.main.common.injection.module

import android.content.Context

import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val context: Context) {

    @Provides
    internal fun provideContext(): Context {
        return context
    }
}
