package org.kidinov.justweather.main.common.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class City(
        @PrimaryKey var id: Long = 0,
        var weather: RealmList<Weather> = RealmList(),
        var main: Main? = null,
        var dt: Int = 0,
        var sys: Sys? = null,
        var name: String = "",
        var currentLocationCity: Boolean = false
) : RealmObject()


open class Weather(
        @PrimaryKey var id: Long = 0,
        var main: String? = null,
        var icon: String? = null
) : RealmObject()

open class Main(
        var temp: Double? = null
) : RealmObject()

open class Sys(
        @PrimaryKey var id: Long = 0,
        var type: Int? = null,
        var country: String? = null
) : RealmObject()
