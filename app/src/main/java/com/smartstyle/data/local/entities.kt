package com.smartstyle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 0,
    val favouriteStyle: String? = null,
    val preferredBrands: String? = null
)

@Entity(tableName = "saved_items")
data class SavedItem(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String
)

