package com.smartstyle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserPreferences::class, SavedItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}