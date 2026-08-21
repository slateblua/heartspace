package com.bluesourceplus.heartspace.data

import com.bluesourceplus.heartspace.data.database.LocalDataSource
import kotlinx.coroutines.flow.Flow

class MoodRepoImpl (private val localDataSource: LocalDataSource) : MoodRepo {
    override fun getById(id: Int): Flow<MoodModel?> {
        return localDataSource.getById(id)
    }

    override fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodModel>> {
        return localDataSource.getMoodsForDate(startMillis, endMillis)
    }
    override suspend fun update(moodModel: MoodModel) {
        localDataSource.update(moodModel)
    }

    override suspend fun deleteById(id: Int) {
        localDataSource.deleteById(id)
    }

    override suspend fun add(moodModel: MoodModel) {
        localDataSource.add(moodModel)
    }
}