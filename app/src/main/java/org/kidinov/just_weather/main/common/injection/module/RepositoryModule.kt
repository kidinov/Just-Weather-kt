package org.kidinov.just_weather.main.common.injection.module


import dagger.Module
import dagger.Provides
import org.kidinov.just_weather.main.common.injection.annotation.Local
import org.kidinov.just_weather.main.common.injection.annotation.Remote
import org.kidinov.just_weather.main.weather.model.WeatherDataSource
import org.kidinov.just_weather.main.weather.model.WeatherRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    internal fun provideWeatherRepository(@Local localDataSource: WeatherDataSource,
                                          @Remote remoteDataSource: WeatherDataSource): WeatherRepository {
        return WeatherRepository(localDataSource, remoteDataSource)
    }
}
