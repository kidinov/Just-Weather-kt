package org.kidinov.justweather.main.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kidinov.justweather.BuildConfig
import org.kidinov.justweather.main.common.remote.ServerInterface
import org.kidinov.justweather.main.util.remote.ApiKeyRequestInterceptor
import org.kidinov.justweather.main.weather.model.WeatherDataSource
import org.kidinov.justweather.main.weather.model.WeatherLocalDataSource
import org.kidinov.justweather.main.weather.model.WeatherRemoteDataSource
import org.kidinov.justweather.main.weather.model.WeatherRepository
import org.kidinov.justweather.main.weather.view.WeatherAdapter
import org.kidinov.justweather.main.weather.viewmodel.WeatherViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
private const val API_KEY = "e746ffbb6021d1959acd9a5bc1986685"
private const val UNITS = "metric"
private const val DATABASE_VERSION = 1

val weatherActivityModule = applicationContext {
    viewModel { WeatherViewModel(get("repo")) }
    factory("local") { WeatherLocalDataSource(get()) as WeatherDataSource }
    factory("remote") { WeatherRemoteDataSource(get()) as WeatherDataSource }
    factory("repo") { WeatherRepository(get("local"), get("remote")) as WeatherDataSource }

    factory { params -> RxPermissions(params["this"]) }
    factory { params -> RxLocation(params["this"]) }
    factory { WeatherAdapter() }

    bean { createRetrofit(BASE_URL, get(), get()) }
    bean { createOkHttp(get()) }
    bean { get<Retrofit>().create(ServerInterface::class.java) }
    bean { ApiKeyRequestInterceptor(API_KEY, UNITS) }
    bean { provideRealm(get()) }
    bean { provideRealmConfig(androidApplication()) }
    bean { GsonBuilder().create() }
}

private fun createOkHttp(apiKeyRequestInterceptor: ApiKeyRequestInterceptor): OkHttpClient {
    val builder = OkHttpClient.Builder()

    builder.addInterceptor(apiKeyRequestInterceptor)

    if (BuildConfig.DEBUG) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(interceptor)
    }

    builder.connectTimeout(5, TimeUnit.SECONDS)
    builder.readTimeout(5, TimeUnit.SECONDS)

    return builder.build()
}

private fun createRetrofit(baseUrl: String, gson: Gson, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
}

private fun provideRealm(config: RealmConfiguration): Realm {
    Realm.setDefaultConfiguration(config)
    return try {
        Realm.getDefaultInstance()
    } catch (e: Exception) {
        Timber.e(e, "")
        Realm.deleteRealm(config)
        Realm.setDefaultConfiguration(config)
        Realm.getDefaultInstance()
    }
}

private fun provideRealmConfig(context: Context): RealmConfiguration {
    Realm.init(context)

    return RealmConfiguration.Builder()
            .schemaVersion(DATABASE_VERSION.toLong())
            .deleteRealmIfMigrationNeeded()
            .build()
}