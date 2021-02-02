package com.dania.productfinder.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.dania.productfinder.api.StoreService
import com.dania.productfinder.db.StoreDb
import com.dania.productfinder.db.ProductDao
import com.dania.productfinder.util.LiveDataCallAdapterFactory

@Module(includes = [ViewModelModule::class])
class AppModule {
    private lateinit var httpClient: OkHttpClient

    @Singleton
    @Provides
    fun provideLiverpoolService(): StoreService {
        var builder: Retrofit.Builder
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient().newBuilder()
                .addInterceptor(interceptor).build()

        return  Retrofit.Builder()
                .baseUrl("https://shoppapp.liverpool.com.mx/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(StoreService::class.java)

    }

    @Singleton
    @Provides
    fun provideDb(app: Application): StoreDb {
        return Room
                .databaseBuilder(app, StoreDb::class.java, "liverpool.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: StoreDb): ProductDao {
        return db.productDao()
    }

}
