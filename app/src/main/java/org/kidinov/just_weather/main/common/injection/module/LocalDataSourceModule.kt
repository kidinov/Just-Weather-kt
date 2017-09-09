package org.kidinov.just_weather.main.common.injection.module


import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import org.kidinov.just_weather.main.common.injection.annotation.Local
import javax.inject.Singleton

@Module
class LocalDataSourceModule {

    @Provides
    @Singleton
    internal fun provideRealmConfig(context: Context): BoxStore {
        return MyObjectBox.builder().androidContext(context).build()
    }

}
