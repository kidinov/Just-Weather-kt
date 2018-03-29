package org.kidinov.justweather.main.weather.model

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.kidinov.justweather.main.common.model.City
import org.kidinov.justweather.main.util.applySchedulers

class WeatherRepository(private val localDataSource: WeatherDataSource,
            private val remoteDataSource: WeatherDataSource) : WeatherDataSource {

    override fun saveOrUpdateCity(city: City): Single<City> {
        throw UnsupportedOperationException()
    }

    override fun addCity(cityName: String): Single<City> {
        return remoteDataSource.addCity(cityName)
                .applySchedulers()
                .flatMap({ localDataSource.saveOrUpdateCity(it) })
    }

    override fun addCity(lat: Double, lon: Double): Single<City> {
        return remoteDataSource.addCity(lat, lon)
                .applySchedulers()
                .flatMap({ city ->
                    city.currentLocationCity = true
                    localDataSource.saveOrUpdateCity(city)
                })
    }

    override fun updateWeatherInfo(): Single<List<City>> {
        return localDataSource.getAddedCitiesWeather()
                .flatMapObservable { Observable.fromIterable(it) }
                //Current location city will be updated when location will be determined
                .filter { city -> !city.currentLocationCity }
                .flatMap { city -> addCity(city.name).toObservable() }
                .toList()
    }

    override fun getAddedCitiesWeather(): Maybe<List<City>> {
        return localDataSource.getAddedCitiesWeather()
    }

    override fun removeCityById(id: Long): Completable {
        return localDataSource.removeCityById(id)
    }
}
