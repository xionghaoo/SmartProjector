package com.ubtrobot.smartprojector.di

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.MqttClient
import com.ubtrobot.smartprojector.repo.ApiService
import com.ubtrobot.smartprojector.core.LiveDataCallAdapterFactory
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.repo.CacheDb
import com.ubtrobot.smartprojector.repo.PreferenceStorage
import com.ubtrobot.smartprojector.repo.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideMqttClient(@ApplicationContext context: Context) : MqttClient {
        return MqttClient(context, Configs.MQTT_SERVER_URI, "app_client_1")
    }

    @Provides @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context) : OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
        client.addInterceptor { chain: Interceptor.Chain ->
            // 发起请求
            val request = chain.request()
            val newRequest = request.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    // 登录接口占位符：invalid，其他接口会有具体的值
//                    ?.addHeader(
//                            preferenceStorage.tokenKey ?: "invalid",
//                            preferenceStorage.accessToken ?: "invalid"
//                    )
//                    ?.addHeader("Api-Key", "admin")
//                    ?.addHeader("Api-Secret", "SGeV1dFmCADUp8XWVpWObO62rIfbpf7Y")
                    .build()
            val response = chain.proceed(newRequest)

            // 接收响应
            val responseCode = response.code
//            if (responseCode == 200) {
//                // 对response做格式过滤，当code不等于0时，只取message的值，忽略掉data的值
//                // 避免data值有格式错误导致gson解析出错
//                val responseString = response.body?.string()
//                var content: String? = responseString
//                try {
//                    val gson = Gson()
//                    val safeData = gson.fromJson<PlainData>(responseString, PlainData::class.java)
//                    if (safeData.code != 0) {
//                        // code 不为0，data数据格式可能不一致
//                        safeData.data = null
//                        content = gson.toJson(safeData)
//                        // 登陆失效
//                        if (safeData.code == 200) {
//                            LoginActivity.startInNewTask(context)
//                        }
//                    } else {
//                        // JSON 能够正确解析
//                        // 正常账号密码登录
//                        if (request?.url.toString().contains("${Configs.HOST}${ApiService.LOGIN}")) {
//                            // 保存AccessToken和TokenKey
//                            val loginResult = Gson().fromJson<LoginData>(
//                                    responseString,
//                                    LoginData::class.java
//                            )
//                            preferenceStorage.accessToken = loginResult.data?.access_token
//                            preferenceStorage.tokenKey = loginResult.data?.token_key
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    val contentType = response.body?.contentType()
//                    val body = ResponseBody.create(
//                            contentType,
//                            content ?: "{\"code\": -1, \"message\": \"未知错误\"}"
//                    )
//                    return@addInterceptor response.newBuilder().body(body).build()
//                }
//            }

            if (responseCode > 500) {
                Toast.makeText(context, "服务器请求错误", Toast.LENGTH_SHORT).show()
            }

            if (responseCode >= 300) {
                val failureUrl = newRequest.url.toString()
                if (failureUrl.isNotEmpty()) {
                    Timber.d("request failure: " + responseCode.toString() + ", " + failureUrl)
                }
            }
            return@addInterceptor response
        }
        return client.build()
    }

    @Provides @Singleton
    fun provideApiService(client: OkHttpClient) : ApiService =
        Retrofit.Builder()
            .baseUrl(Configs.HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)

    @Provides @Singleton
    fun provideSharedPreferences(application: Application) : PreferenceStorage =
        SharedPreferenceStorage(application)

    @Provides @Singleton
    fun provideConnectionStateMonitor() = ConnectionStateMonitor()

    @Provides @Singleton
    fun provideCacheDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CacheDb::class.java, "smart-projector-cache.db")
            .fallbackToDestructiveMigration()
//            .addMigrations(MIGRATION_28_29)
            .build()
}