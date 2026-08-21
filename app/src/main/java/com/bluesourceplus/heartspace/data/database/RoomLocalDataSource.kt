package com.bluesourceplus.heartspace.data.database

import com.bluesourceplus.heartspace.data.MoodModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalDataSource (private val dao: MoodDao): LocalDataSource {
    override fun getById(id: Int): Flow<MoodModel?> {
        return dao.getById(id).map { moodEnt ->
            moodEnt?.toModel()
        }
    }

    override fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodModel>> {
        return dao.getMoodsForDate(startMillis, endMillis).map { moodEntries ->
            moodEntries.map { it.toModel() }
        }
    }

    override suspend fun update(moodModel: MoodModel) {
        dao.update(moodEntry = moodModel.toEntry())
    }

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun add(moodModel: MoodModel) {
        dao.add(moodEntry = moodModel.toEntry())
    }
}