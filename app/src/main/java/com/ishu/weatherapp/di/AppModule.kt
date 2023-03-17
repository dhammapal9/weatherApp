package com.ishu.weatherapp.di

import com.google.gson.GsonBuilder
import com.ishu.weatherapp.data.remote.ContentService
import com.ishu.weatherapp.utils.ApiHandle
import com.ishu.weatherapp.utils.MyRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            //.readTimeout(15, TimeUnit.SECONDS)
            //.connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(MyRequestInterceptor())
            .addInterceptor(OkHttpProfilerInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiHandle.base_api_url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create( GsonBuilder()
                .setLenient()
                .create()))
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()



    @Singleton
    @Provides
    fun provideContentService(retrofit: Retrofit): ContentService =
        retrofit.create(ContentService::class.java)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

