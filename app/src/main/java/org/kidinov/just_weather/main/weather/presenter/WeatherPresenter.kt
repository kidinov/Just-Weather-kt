package org.kidinov.just_weather.main.weather.presenter


import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.kidinov.just_weather.main.common.model.City
import org.kidinov.just_weather.main.weather.WeatherContract
import org.kidinov.just_weather.main.weather.model.WeatherRepository
import timber.log.Timber
import javax.inject.Inject

class WeatherPresenter
@Inject
constructor(private val repository: WeatherRepository, private val view: WeatherContract.View) : WeatherContract.Presenter {
    private val subscriptions = CompositeDisposable()

    override fun updateData(refresh: Boolean) {
        if (!refresh) {
            view.showProgress()
        }
        subscriptions.add(repository.updateWeatherInfo()
                .subscribe({
                    Timber.d("weather updated")
                    getDataFromDb(false)
                }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    view.showNetworkErrorNotification()
                })
        )
    }

    override fun addCityByCoordinates(latitude: Double, longitude: Double) {
        subscriptions.add(repository.addCity(latitude, longitude)
                .subscribe({
                    Timber.d("city added - $it")
                    getDataFromDb(false)
                }, {
                    Timber.e(it)
                    getDataFromDb(true)
                })
        )
    }

    override fun addCityByName(name: String) {
        view.showProgress()
        subscriptions.add(repository.addCity(name)
                .subscribe({
                    Timber.d("city added - $it")
                    getDataFromDb(false)
                }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    view.showNetworkErrorNotification()
                })
        )
    }

    override fun itemRemovedAtPosition(swipedPosition: Int) {
        subscriptions.add(getSortedListOfCities()
                .flatMapCompletable { cities ->
                    if (!cities.isEmpty()) {
                        val cityToDelete = cities[swipedPosition]
                        if (cityToDelete.currentLocationCity) {
                            view.showCurrentCityDeletionErrorNotification()
                            Completable.complete()
                        }
                        repository.removeCityById(cityToDelete.id)
                    }
                    Completable.complete()
                }
                .subscribe({
                    Timber.d("city removed")
                    getDataFromDb(false)
                }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    view.showNetworkErrorNotification()
                }))
    }

    private fun getDataFromDb(error: Boolean) {
        subscriptions.add(getSortedListOfCities()
                .subscribe({ cities ->
                    if (cities.isEmpty()) {
                        if (error) {
                            view.showError()
                        } else {
                            view.showEmptyState()
                        }
                    } else {
                        view.showData(cities)
                    }
                }, { Timber.e(it) }))
    }

    private fun getSortedListOfCities(): Single<List<City>> {
        return repository.getAddedCitiesWeather()
                .flatMapObservable({ Observable.fromIterable(it) })
                .toSortedList({ w1, w2 ->
                    if (w1.currentLocationCity) -1
                    if (w2.currentLocationCity) 1
                    w1.name.compareTo(w2.name)
                })
    }

    override fun subscribe() {}

    override fun unsubscribe() {
        subscriptions.dispose()
    }
}
