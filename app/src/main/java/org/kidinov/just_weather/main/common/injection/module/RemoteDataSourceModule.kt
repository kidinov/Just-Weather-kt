package org.kidinov.just_weather.main.common.injection.module


import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kidinov.just_weather.BuildConfig
import org.kidinov.just_weather.main.common.remote.ServerInterface
import org.kidinov.just_weather.main.util.remote.ApiKeyRequestInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RemoteDataSourceModule {
    private val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    private val API_KEY = "e746ffbb6021d1959acd9a5bc1986685"
    private val UNITS = "metric"

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        return getOkHttpClient(apiKeyRequestInterceptor)
    }

    @Provides
    @Singleton
    internal fun provideServerInterface(moshi: Moshi, okHttpClient: OkHttpClient): ServerInterface {
        return getServerInterface(getRetrofit(moshi, okHttpClient))
    }

    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    internal fun provideApiKeyRequestInterceptor(): ApiKeyRequestInterceptor {
        return apiKeyRequestInterceptor
    }

    fun getRetrofit(gson: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

    fun getOkHttpClient(apiKeyRequestInterceptor: ApiKeyRequestInterceptor): OkHttpClient {
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

    fun getServerInterface(retrofit: Retrofit): ServerInterface {
        return retrofit.create(ServerInterface::class.java)
    }

    val apiKeyRequestInterceptor: ApiKeyRequestInterceptor
        get() = ApiKeyRequestInterceptor(API_KEY, UNITS)

}
