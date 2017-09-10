package org.kidinov.just_weather.main.weather.view

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_item.view.*
import org.kidinov.just_weather.R
import org.kidinov.just_weather.main.common.model.City
import org.kidinov.just_weather.main.util.getDrawableByFileName
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class WeatherAdapter
@Inject
internal constructor() : RecyclerView.Adapter<WeatherAdapter.WeatherInCityViewHolder>() {
    private var weatherInCities: List<City> = ArrayList()

    fun setWeatherInCities(weatherInCities: List<City>) {
        this.weatherInCities = weatherInCities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherInCityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return WeatherInCityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeatherInCityViewHolder, position: Int) {
        val city = weatherInCities[position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return weatherInCities.size
    }

    class WeatherInCityViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        @SuppressLint("DefaultLocale", "SetTextI18n")
        fun bind(city: City) {
            val context = itemView.context
            if (!city.weather.isEmpty()) {
                itemView.ivPic.setImageDrawable(getDrawableByFileName(context, "ic_${city.weather.first().icon}"))
            }
            itemView.tvLocation.text = "${city.name}/${city.sys?.country}"
            if (city.currentLocationCity) {
                itemView.tvLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context,
                        R.drawable.ic_my_location_gray_16dp), null)
            } else {
                itemView.tvLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            itemView.tvTemperature.text = "${city.main?.temp?.toInt()}°"
            val date = Date(city.dt.toLong() * 1000)
            itemView.tvDate.text = "${DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date)} " +
                    "${DateFormat.getTimeInstance(DateFormat.SHORT).format(date)})"
        }
    }
}
