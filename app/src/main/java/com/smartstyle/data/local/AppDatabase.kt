package com.smartstyle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OutfitAnalysisEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun outfitAnalysisDao(): OutfitAnalysisDao
}