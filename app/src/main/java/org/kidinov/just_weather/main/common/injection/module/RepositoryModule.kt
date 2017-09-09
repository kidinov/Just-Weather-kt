package org.kidinov.just_weather.main.common.injection.module

import org.kidinov.just_weather.common.injection.annotation.Local
import org.kidinov.just_weather.common.injection.annotation.Remote
import org.kidinov.just_weather.weather.model.WeatherDataSource
import org.kidinov.just_weather.weather.model.WeatherRepository

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Singleton
    @Provides
    internal fun provideWeatherRepository(@Local localDataSource: WeatherDataSource,
                                          @Remote remoteDataSource: WeatherDataSource): WeatherRepository {
        return WeatherRepository(localDataSource, remoteDataSource)
    }
}
