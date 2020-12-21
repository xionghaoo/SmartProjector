package com.ubtrobot.smartprojector.di

import android.content.Context
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.MqttClient
import com.ubtrobot.smartprojector.repo.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideMqttClient(@ApplicationContext context: Context) : MqttClient {
        return MqttClient(context, Configs.MQTT_SERVER_URI, "app_client_1")
    }

    @Provides
    fun provideOkHttpClient() : OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .build()
    }

    @Provides
    fun provideApiService(client: OkHttpClient) : ApiService =
        Retrofit.Builder()
            .baseUrl(Configs.HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}