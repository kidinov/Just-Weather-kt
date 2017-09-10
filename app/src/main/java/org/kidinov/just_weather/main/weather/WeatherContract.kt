package org.kidinov.just_weather.main.weather

import org.kidinov.just_weather.main.common.base.BasePresenter
import org.kidinov.just_weather.main.common.base.BaseView
import org.kidinov.just_weather.main.common.model.City


interface WeatherContract {
    interface View : BaseView {
        fun showProgress()

        fun showData(cities: List<City>)

        fun showError()

        fun hideItemAtPosition(position: Int)

        fun showEmptyState()

        fun showNetworkErrorNotification()

        fun showCurrentCityDeletionErrorNotification()
    }

    interface Presenter : BasePresenter {
        fun updateData(refresh: Boolean)

        fun addCityByCoordinates(latitude: Double, longitude: Double)

        fun addCityByName(name: String)

        fun itemRemovedAtPosition(swipedPosition: Int)
    }
}
