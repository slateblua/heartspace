package com.bluesourceplus.heartspace.data.database

import com.bluesourceplus.heartspace.data.MoodModel
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getById(id: Int): Flow<MoodModel?>

    fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodModel>>

    suspend fun update(moodModel: MoodModel)

    suspend fun deleteById(id: Int)

    suspend fun add(moodModel: MoodModel)
}