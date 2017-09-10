package org.kidinov.just_weather.main.weather.presenter


import dagger.Module
import dagger.Provides
import org.kidinov.just_weather.main.common.injection.annotation.ActivityScope
import org.kidinov.just_weather.main.weather.WeatherContract
import org.kidinov.just_weather.main.weather.view.WeatherActivity

@ActivityScope
@Module
class WeatherPresenterModule(private val view: WeatherActivity) {

    @Provides
    internal fun provideView(): WeatherContract.View {
        return view
    }
}
