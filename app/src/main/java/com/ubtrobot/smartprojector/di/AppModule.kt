package com.ubtrobot.smartprojector.di

import android.content.Context
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.MqttClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideMqttClient(@ApplicationContext context: Context) : MqttClient {
        return MqttClient(context, Configs.MQTT_SERVER_URI, "app_client_1")
    }
}