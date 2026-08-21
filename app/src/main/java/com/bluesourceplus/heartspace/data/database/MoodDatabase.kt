package com.bluesourceplus.heartspace.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDate

@Database(entities = [MoodEntry::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun getMoodDao(): MoodDao
}

@Suppress("unused")
class LocalDateConverters {
    @TypeConverter
    fun fromLong(value: Long): LocalDate {
        return LocalDate.fromEpochDays(value.toInt())
    }

    @TypeConverter
    fun toLong(value: LocalDate): Long {
        return value.toEpochDays().toLong()
    }
}
