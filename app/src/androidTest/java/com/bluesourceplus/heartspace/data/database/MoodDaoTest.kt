package com.bluesourceplus.heartspace.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bluesourceplus.heartspace.data.Mood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MoodDaoTest {
    private lateinit var database: MoodDatabase
    private lateinit var dao: MoodDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MoodDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.getMoodDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUpdateAndDeleteById() = runTest {
        dao.add(MoodEntry(id = 1, mood = Mood.HAPPY.name, timestamp = 100L, note = "first", imageUri = null))

        dao.update(MoodEntry(id = 1, mood = Mood.SAD.name, timestamp = 100L, note = "updated", imageUri = null))
        assertEquals("updated", dao.getById(1).first()?.note)

        dao.deleteById(1)

        assertNull(dao.getById(1).first())
    }

    @Test
    fun getMoodsForDateReturnsOnlyRangeInTimestampDescendingOrder() = runTest {
        dao.add(MoodEntry(id = 1, mood = Mood.HAPPY.name, timestamp = 100L, note = "before", imageUri = null))
        dao.add(MoodEntry(id = 2, mood = Mood.SAD.name, timestamp = 200L, note = "inside older", imageUri = null))
        dao.add(MoodEntry(id = 3, mood = Mood.EXCITED.name, timestamp = 250L, note = "inside newer", imageUri = null))
        dao.add(MoodEntry(id = 4, mood = Mood.TIRED.name, timestamp = 300L, note = "end exclusive", imageUri = null))

        val entries = dao.getMoodsForDate(startMillis = 200L, endMillis = 300L).first()

        assertEquals(listOf(3, 2), entries.map { it.id })
    }

    @Test
    fun moodBreakdownCalculatesPercentages() = runTest {
        dao.add(MoodEntry(id = 1, mood = Mood.HAPPY.name, timestamp = 100L, note = "", imageUri = null))
        dao.add(MoodEntry(id = 2, mood = Mood.HAPPY.name, timestamp = 200L, note = "", imageUri = null))
        dao.add(MoodEntry(id = 3, mood = Mood.SAD.name, timestamp = 300L, note = "", imageUri = null))

        val breakdown = dao.getMoodBreakdown()

        assertEquals("HAPPY", breakdown.first().mood)
        assertEquals(2, breakdown.first().count)
        assertEquals(66.67, breakdown.first().percentage, 0.01)
    }

    @Test
    fun unknownStoredMoodMapsSafely() {
        val model = MoodEntry(id = 1, mood = "RENAMED", timestamp = 100L, note = "", imageUri = null).toModel()

        assertEquals(Mood.UNKNOWN, model.mood)
    }
}
