package org.kidinov.just_weather.main.common.injection.module


import android.content.Context
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import org.kidinov.just_weather.main.common.injection.annotation.Local
import org.kidinov.just_weather.main.weather.model.WeatherDataSource
import org.kidinov.just_weather.main.weather.model.WeatherLocalDataSource
import timber.log.Timber
import javax.inject.Singleton

@Module
class LocalDataSourceModule {
    private val DATABASE_VERSION = 1

    @Singleton
    @Provides
    @Local
    internal fun provideWeatherDataSource(realm: Realm): WeatherDataSource {
        return WeatherLocalDataSource(realm)
    }

    @Provides
    @Singleton
    internal fun provideRealm(config: RealmConfiguration): Realm {
        Realm.setDefaultConfiguration(config)
        try {
            return Realm.getDefaultInstance()
        } catch (e: Exception) {
            Timber.e(e, "")
            Realm.deleteRealm(config)
            Realm.setDefaultConfiguration(config)
            return Realm.getDefaultInstance()
        }

    }

    @Provides
    @Singleton
    internal fun provideRealmConfig(context: Context): RealmConfiguration {
        Realm.init(context)

        return RealmConfiguration.Builder()
                .schemaVersion(DATABASE_VERSION.toLong())
                .deleteRealmIfMigrationNeeded()
                .build()
    }

}
