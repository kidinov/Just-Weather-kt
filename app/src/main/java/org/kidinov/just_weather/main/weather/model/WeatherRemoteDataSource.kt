package org.kidinov.just_weather.main.weather.model


import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.kidinov.just_weather.main.common.model.City
import org.kidinov.just_weather.main.common.remote.ServerInterface

class WeatherRemoteDataSource(private val serverInterface: ServerInterface) : WeatherDataSource {

    override fun saveOrUpdateCity(city: City): Single<City> {
        throw UnsupportedOperationException()
    }

    override fun addCity(cityName: String): Single<City> {
        return serverInterface.getWeather(cityName)
    }

    override fun addCity(lat: Double, lon: Double): Single<City> {
        return serverInterface.getWeather(lat, lon)
    }

    override fun updateWeatherInfo(): Single<List<City>> {
        throw UnsupportedOperationException()
    }

    override fun getAddedCitiesWeather(): Maybe<List<City>> {
        throw UnsupportedOperationException()
    }

    override fun removeCityById(id: Long): Completable {
        throw UnsupportedOperationException()
    }
}
