package org.kidinov.just_weather.main.common.injection.component


import dagger.Component
import org.kidinov.just_weather.main.App
import org.kidinov.just_weather.main.common.injection.module.ApplicationModule
import org.kidinov.just_weather.main.common.injection.module.LocalDataSourceModule
import org.kidinov.just_weather.main.common.injection.module.RemoteDataSourceModule
import org.kidinov.just_weather.main.common.injection.module.RepositoryModule
import org.kidinov.just_weather.main.weather.model.WeatherRepository
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, RepositoryModule::class,
        LocalDataSourceModule::class, RemoteDataSourceModule::class))
interface RepositoryComponent {
    fun getWeatherRepository(): WeatherRepository

    fun inject(o: App)
}
