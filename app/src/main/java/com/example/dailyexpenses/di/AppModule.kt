package com.example.dailyexpenses.di

import android.app.Application
import androidx.room.Room
import com.example.dailyexpenses.api.FirebaseCloudMessagingApi
import com.example.dailyexpenses.api.ServiceApi
import com.example.dailyexpenses.data.ItemToBuyDatabase
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, ItemToBuyDatabase::class.java, "itemToBuy_database")
//            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideItemToBuyDao(db: ItemToBuyDatabase) = db.itemToBuyDao()

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


    @Provides
    @Singleton
    @DailyExpenses
    fun provideRetrofit(): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        builder.networkInterceptors().add(httpLoggingInterceptor)
        val okHttpClient = builder.build()

        return Retrofit.Builder()
            .baseUrl(ServiceApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Fcm
    fun provideRetrofitFcm(): Retrofit{
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        builder.networkInterceptors().add(httpLoggingInterceptor)
        val okHttpClient = builder.build()

        return Retrofit.Builder()
            .baseUrl(FirebaseCloudMessagingApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideServiceApi(@DailyExpenses retrofit: Retrofit): ServiceApi =
        retrofit.create(ServiceApi::class.java)

    @Provides
    @Singleton
    fun provideFcmApi(@Fcm retrofit: Retrofit): FirebaseCloudMessagingApi =
        retrofit.create(FirebaseCloudMessagingApi::class.java)
}

@Qualifier
annotation class DailyExpenses

@Qualifier
annotation class Fcm