package org.kidinov.just_weather.main.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat


fun getDrawableByFileName(context: Context, iconNum: String): Drawable {
    val resources = context.resources
    val resourceId = resources.getIdentifier(iconNum, "drawable", context.packageName)
    return ContextCompat.getDrawable(context, resourceId)
}