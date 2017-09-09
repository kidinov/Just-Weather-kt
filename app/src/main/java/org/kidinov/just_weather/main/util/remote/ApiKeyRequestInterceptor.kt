package org.kidinov.just_weather.main.util.remote

import okhttp3.Interceptor
import okhttp3.Response


class ApiKeyRequestInterceptor(val apiKey: String, val units: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
                .addQueryParameter("APPID", apiKey)
                .addQueryParameter("units", units)
                .build()

        val request = original.newBuilder()
                .url(url)
                .build()

        return chain.proceed(request)
    }

}