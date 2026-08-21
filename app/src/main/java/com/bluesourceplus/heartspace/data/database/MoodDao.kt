package com.bluesourceplus.heartspace.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM moodentry WHERE id = :id")
    fun getById(id: Int): Flow<MoodEntry?>

    @Query("SELECT * FROM moodentry WHERE timestamp >= :startMillis AND timestamp < :endMillis ORDER BY timestamp DESC")
    fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodEntry>>

    @Update
    suspend fun update(moodEntry: MoodEntry)

    @Query("DELETE FROM moodentry WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert
    suspend fun add(moodEntry: MoodEntry)
}