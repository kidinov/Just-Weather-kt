package org.kidinov.justweather.main.weather.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.kidinov.justweather.main.common.model.City
import org.kidinov.justweather.main.util.record
import org.kidinov.justweather.main.weather.model.WeatherDataSource
import timber.log.Timber


class WeatherViewModel(private val dataSource: WeatherDataSource) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val state = MutableLiveData<UiState>()

    fun onRefresh(refresh: Boolean) {
        if (!refresh) {
            state.value = ProgressState
        }

        dataSource.updateWeatherInfo()
                .subscribe({ getDataFromDb(false) }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    state.value = NetworkErrorState
                }).record(compositeDisposable)
    }

    fun onAddCityByCoordinates(latitude: Double, longitude: Double) {
        dataSource.addCity(latitude, longitude)
                .subscribe({ getDataFromDb(false) }, {
                    Timber.e(it)
                    getDataFromDb(true)
                }).record(compositeDisposable)
    }

    fun onAddCityByName(name: String) {
        state.value = ProgressState
        dataSource.addCity(name)
                .subscribe({ getDataFromDb(false) }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    state.value = NetworkErrorState
                }).record(compositeDisposable)
    }

    fun onItemRemovedAtPosition(swipedPosition: Int) {
        getSortedListOfCities()
                .flatMapCompletable { cities ->
                    if (!cities.isEmpty()) {
                        val cityToDelete = cities[swipedPosition]
                        if (cityToDelete.currentLocationCity) {
                            state.value = CurrentCityDeletionNotification
                            Completable.complete()
                        }
                        dataSource.removeCityById(cityToDelete.id)
                    }
                    Completable.complete()
                }
                .subscribe({
                    Timber.d("city removed")
                    getDataFromDb(false)
                }, {
                    Timber.e(it)
                    getDataFromDb(true)
                    state.value = NetworkErrorState
                }).record(compositeDisposable)
    }

    private fun getDataFromDb(error: Boolean) {
        getSortedListOfCities()
                .subscribe({ cities ->
                    if (cities.isEmpty()) {
                        if (error) {
                            state.value = ErrorState
                        } else {
                            state.value = EmptyState
                        }
                    } else {
                        state.value = ListState(cities)
                    }
                }, { Timber.e(it) })
                .record(compositeDisposable)
    }

    private fun getSortedListOfCities() =
            dataSource.getAddedCitiesWeather()
                    .flatMapObservable({ Observable.fromIterable(it) })
                    .toSortedList({ w1, w2 ->
                        if (w1.currentLocationCity) -1
                        if (w2.currentLocationCity) 1
                        w1.name.compareTo(w2.name)
                    })

    override fun onCleared() {
        compositeDisposable.clear()
    }
}

sealed class UiState
object EmptyState : UiState()
object ErrorState : UiState()
object NetworkErrorState : UiState()
object ProgressState : UiState()
object CurrentCityDeletionNotification : UiState()
data class ListState(val cities: List<City>) : UiState()