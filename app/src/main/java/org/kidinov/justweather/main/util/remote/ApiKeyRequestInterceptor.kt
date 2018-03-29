package org.kidinov.justweather.main.util.remote

import okhttp3.Interceptor
import okhttp3.Response


class ApiKeyRequestInterceptor(private val apiKey: String,
                               private val units: String) : Interceptor {

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