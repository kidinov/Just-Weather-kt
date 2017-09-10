package org.kidinov.just_weather.main.weather.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.weather_add_city_dialog.*
import kotlinx.android.synthetic.main.weather_add_city_dialog.view.*
import org.kidinov.just_weather.R

class WeatherAddCityDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.weather_add_city_dialog, null)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.add_city)
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(android.R.string.ok) { _, _ -> (activity as WeatherActivity).addCityByName(view.etCityName.text.toString()) }
        builder.setView(view)
        val dialog = builder.create()

        dialog.setOnShowListener {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etCityName, InputMethodManager.SHOW_IMPLICIT)
        }

        return dialog
    }

    companion object {

        fun newInstance(): WeatherAddCityDialog {
            return WeatherAddCityDialog()
        }
    }

}
