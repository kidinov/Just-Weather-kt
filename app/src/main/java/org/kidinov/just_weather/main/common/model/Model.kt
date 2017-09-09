package org.kidinov.just_weather.main.common.model
import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class City(
        @Id val id: Int,
        val weather: List<Weather>,
        val main: Main,
        val dt: Int,
        val sys: Sys,
        val name: String
)

@Entity
data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String? = null)

@Entity
data class Main(
        val temp: Double,
        val pressure: Double,
        val humidity: Int,
        @Json(name = "temp_min") val tempMin: Double,
        @Json(name = "temp_max") val tempMax: Double)

@Entity
data class Sys(
        val id: Int,
        val type: Int,
        val message: Double,
        val country: String,
        val sunrise: Int,
        val sunset: Int)
