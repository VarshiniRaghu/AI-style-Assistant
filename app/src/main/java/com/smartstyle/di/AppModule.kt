package com.smartstyle.di

import android.content.Context
import androidx.room.Room
import com.smartstyle.data.local.AppDatabase
import com.smartstyle.data.local.UserDao
import com.smartstyle.data.remote.OpenAIService
import com.smartstyle.data.repository.AIRepository
import com.smartstyle.data.repository.AIRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/") // replace with chosen AI API base
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides @Singleton
    fun provideOpenAIService(retrofit: Retrofit): OpenAIService = retrofit.create(OpenAIService::class.java)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "smartstyle-db").build()

    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides @Singleton
    fun provideAIRepository(
        openAIService: OpenAIService,
        @ApplicationContext context: Context
    ): AIRepository = AIRepositoryImpl(openAIService, context)
}
