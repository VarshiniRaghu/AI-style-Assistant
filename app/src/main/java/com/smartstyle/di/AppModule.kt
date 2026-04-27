package com.smartstyle.di

import android.content.Context
import androidx.room.Room
import com.smartstyle.BuildConfig
import com.smartstyle.data.local.AppDatabase
import com.smartstyle.data.local.OutfitAnalysisDao
import com.smartstyle.data.remote.GeminiService
import com.smartstyle.data.repository.OutfitRepository
import com.smartstyle.data.repository.OutfitRepositoryImpl
import com.smartstyle.ml.MlKitImageAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "outfit-analyser-db").build()

    @Provides
    fun provideOutfitAnalysisDao(db: AppDatabase): OutfitAnalysisDao = db.outfitAnalysisDao()

    @Provides
    @Singleton
    fun provideGeminiService(): GeminiService = GeminiService(BuildConfig.GEMINI_API_KEY)

    @Provides
    @Singleton
    fun provideOutfitRepository(
        geminiService: GeminiService,
        mlKitAnalyzer: MlKitImageAnalyzer,
        dao: OutfitAnalysisDao,
        @ApplicationContext context: Context
    ): OutfitRepository = OutfitRepositoryImpl(geminiService, mlKitAnalyzer, dao, context)
}
