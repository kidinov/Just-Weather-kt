package org.kidinov.just_weather.main.common.remote

import io.reactivex.Single
import org.kidinov.just_weather.main.common.model.City
import retrofit2.http.Query


interface ServerInterface {
    fun getWeather(@Query("q") cityName : String) : Single<City>
}