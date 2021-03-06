package com.example.dailyexpenses.di

import android.app.Application
import androidx.room.Room
import com.example.dailyexpenses.api.ServiceApi
import com.example.dailyexpenses.data.ItemToBuyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, ItemToBuyDatabase::class.java, "itemToBuy_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideItemToBuyDao(db: ItemToBuyDatabase) = db.itemToBuyDao()

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
            Retrofit.Builder()
                .baseUrl(ServiceApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): ServiceApi =
        retrofit.create(ServiceApi::class.java)
}