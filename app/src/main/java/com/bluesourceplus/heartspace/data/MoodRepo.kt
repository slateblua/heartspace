package com.bluesourceplus.heartspace.data

import kotlinx.coroutines.flow.Flow

interface MoodRepo {
    fun getById(id: Int): Flow<MoodModel?>

    fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodModel>>

    suspend fun update(moodModel: MoodModel)

    suspend fun deleteById(id: Int)

    suspend fun add(moodModel: MoodModel)
}