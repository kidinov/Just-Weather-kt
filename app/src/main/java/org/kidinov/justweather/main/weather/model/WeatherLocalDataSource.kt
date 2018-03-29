package org.kidinov.justweather.main.weather.model

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.realm.Realm
import org.kidinov.justweather.main.common.model.City

class WeatherLocalDataSource(private val realm: Realm) : WeatherDataSource {


    override fun getAddedCitiesWeather(): Maybe<List<City>> {
        val cities = realm.where(City::class.java).findAll()
        return if (cities.isEmpty()) Maybe.empty() else Maybe.just(realm.copyFromRealm(cities))
    }

    override fun saveOrUpdateCity(city: City): Single<City> {
        realm.executeTransaction {
            if (city.currentLocationCity) {
                val currentCity = realm.where(City::class.java)
                        .equalTo("currentLocationCity", true)
                        .findFirst()
                currentCity?.deleteFromRealm()
            } else {
                val savedCity = realm.where(City::class.java)
                        .equalTo("id", city.id)
                        .findFirst()
                if (savedCity != null) {
                    city.currentLocationCity = savedCity.currentLocationCity
                }
            }
            realm.copyToRealmOrUpdate(city)
        }
        return Single.just(city)
    }

    override fun addCity(cityName: String): Single<City> {
        throw UnsupportedOperationException()
    }

    override fun addCity(lat: Double, lon: Double): Single<City> {
        throw UnsupportedOperationException()
    }

    override fun updateWeatherInfo(): Single<List<City>> {
        throw UnsupportedOperationException()
    }

    override fun removeCityById(id: Long): Completable {
        return Completable.fromAction {
            realm.executeTransaction {
                val toRemove = realm.where(City::class.java).equalTo("id", id).findFirst()
                toRemove?.deleteFromRealm()
            }
        }
    }
}
