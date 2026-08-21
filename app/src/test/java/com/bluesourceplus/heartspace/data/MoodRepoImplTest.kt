package com.bluesourceplus.heartspace.data

import com.bluesourceplus.heartspace.data.database.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MoodRepoImplTest {
    @Test
    fun deleteByIdPassesIdToLocalDataSource() = runTest {
        val localDataSource = FakeLocalDataSource()
        val repo = MoodRepoImpl(localDataSource)

        repo.deleteById(42)

        assertEquals(42, localDataSource.deletedId)
    }

    @Test
    fun getMoodsForDatePassesRangeToLocalDataSource() = runTest {
        val localDataSource = FakeLocalDataSource()
        val repo = MoodRepoImpl(localDataSource)

        repo.getMoodsForDate(100L, 200L).first()

        assertEquals(100L to 200L, localDataSource.requestedDateRange)
    }

    private class FakeLocalDataSource : LocalDataSource {
        var deletedId: Int? = null
        var requestedDateRange: Pair<Long, Long>? = null

        override fun getById(id: Int): Flow<MoodModel?> = emptyFlow()

        override fun getMoodsForDate(startMillis: Long, endMillis: Long): Flow<List<MoodModel>> {
            requestedDateRange = startMillis to endMillis
            return flowOf(emptyList())
        }

        override suspend fun update(moodModel: MoodModel) = Unit

        override suspend fun deleteById(id: Int) {
            deletedId = id
        }

        override suspend fun add(moodModel: MoodModel) = Unit

        override suspend fun getMoodBreakdown(): List<MoodBreakdown> = emptyList()
    }
}
