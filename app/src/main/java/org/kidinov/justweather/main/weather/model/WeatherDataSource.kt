package org.kidinov.justweather.main.weather.model

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.kidinov.justweather.main.common.model.City


interface WeatherDataSource {
    fun updateWeatherInfo(): Single<List<City>>

    fun getAddedCitiesWeather(): Maybe<List<City>>

    fun saveOrUpdateCity(city: City): Single<City>

    fun addCity(cityName: String): Single<City>

    fun addCity(lat: Double, lon: Double): Single<City>

    fun removeCityById(id: Long): Completable
}
