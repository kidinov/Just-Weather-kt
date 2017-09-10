package org.kidinov.just_weather.main.weather


import dagger.Component
import org.kidinov.just_weather.main.common.injection.annotation.ActivityScope
import org.kidinov.just_weather.main.common.injection.component.RepositoryComponent
import org.kidinov.just_weather.main.weather.presenter.WeatherPresenterModule
import org.kidinov.just_weather.main.weather.view.WeatherActivity
import org.kidinov.just_weather.main.weather.view.WeatherActivityModule

@ActivityScope
@Component(dependencies = arrayOf(RepositoryComponent::class), modules = arrayOf(WeatherPresenterModule::class, WeatherActivityModule::class))
interface WeatherComponent {
    fun inject(o: WeatherActivity)
}
