package org.kidinov.just_weather.main.common.remote

import io.reactivex.Single
import org.kidinov.just_weather.main.common.model.City
import retrofit2.http.GET
import retrofit2.http.Query


interface ServerInterface {
    @GET("weather")
    fun getWeather(@Query("q") cityName : String) : Single<City>

    @GET("weather")
    fun getWeather(@Query("lat") lat : Double, @Query("lon") lot : Double) : Single<City>
}