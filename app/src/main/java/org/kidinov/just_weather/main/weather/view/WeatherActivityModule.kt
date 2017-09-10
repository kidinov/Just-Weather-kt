package org.kidinov.just_weather.main.weather.view

import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions

import dagger.Module
import dagger.Provides

@Module
class WeatherActivityModule(private val activity: WeatherActivity) {

    @Provides
    internal fun provideRxPermissions(): RxPermissions {
        return RxPermissions(activity)
    }

    @Provides
    internal fun provideRxLocation(): RxLocation {
        return RxLocation(activity)
    }
}
