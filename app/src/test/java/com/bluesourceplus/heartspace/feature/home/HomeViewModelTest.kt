package com.bluesourceplus.heartspace.feature.home

import com.bluesourceplus.heartspace.MainDispatcherRule
import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.DeleteMoodUseCase
import com.bluesourceplus.heartspace.feature.home.usecases.GetMoodsForDateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.DateTimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val timeZone = TimeZone.UTC
    private val today = LocalDate(2026, 6, 3)

    @Test
    fun initialDateIsTodayAndQueriesThatDay() = runTest {
        val getMoods = FakeGetMoodsForDateUseCase()
        val viewModel = createViewModel(getMoods = getMoods)

        collectState(viewModel)
        advanceUntilIdle()

        assertEquals(today, viewModel.state.value.selectedDate)
        assertFalse(viewModel.state.value.canGoNext)
        assertEquals(
            today.atStartOfDayIn(timeZone).toEpochMilliseconds() to
                today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds(),
            getMoods.calls.last(),
        )
    }

    @Test
    fun previousAndNextDayUpdateSelectedDate() = runTest {
        val viewModel = createViewModel()
        collectState(viewModel)

        viewModel.handleEvent(HomeScreenIntent.GoToPreviousDay)
        advanceUntilIdle()

        assertEquals(LocalDate(2026, 6, 2), viewModel.state.value.selectedDate)
        assertTrue(viewModel.state.value.canGoNext)

        viewModel.handleEvent(HomeScreenIntent.GoToNextDay)
        advanceUntilIdle()

        assertEquals(today, viewModel.state.value.selectedDate)
        assertFalse(viewModel.state.value.canGoNext)
    }

    @Test
    fun nextDayDoesNothingWhenSelectedDateIsToday() = runTest {
        val viewModel = createViewModel()
        collectState(viewModel)

        viewModel.handleEvent(HomeScreenIntent.GoToNextDay)
        advanceUntilIdle()

        assertEquals(today, viewModel.state.value.selectedDate)
    }

    @Test
    fun selectedDateEmptyStateIsRepresentedByEmptyMoodList() = runTest {
        val viewModel = createViewModel()
        collectState(viewModel)
        advanceUntilIdle()

        assertEquals(today, viewModel.state.value.selectedDate)
        assertTrue(viewModel.state.value.moods.isEmpty())
    }

    @Test
    fun deleteMoodUsesIdDirectly() = runTest {
        val deleteMood = FakeDeleteMoodUseCase()
        val viewModel = createViewModel(deleteMood = deleteMood)

        viewModel.handleEvent(HomeScreenIntent.DeleteMood(7))
        advanceUntilIdle()

        assertEquals(listOf(7), deleteMood.deletedIds)
    }

    private fun createViewModel(
        getMoods: FakeGetMoodsForDateUseCase = FakeGetMoodsForDateUseCase(),
        deleteMood: FakeDeleteMoodUseCase = FakeDeleteMoodUseCase(),
    ): HomeViewModel {
        return HomeViewModel(
            getMoodsForDateUseCase = getMoods,
            deleteMoodUseCase = deleteMood,
            timeZone = timeZone,
            now = { today.atStartOfDayIn(timeZone) },
        )
    }

    private fun TestScope.collectState(viewModel: HomeViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
    }

    private class FakeGetMoodsForDateUseCase : GetMoodsForDateUseCase {
        val calls = mutableListOf<Pair<Long, Long>>()
        private val moods = MutableStateFlow<List<MoodModel>>(emptyList())

        override fun invoke(startMillis: Long, endMillis: Long): Flow<List<MoodModel>> {
            calls += startMillis to endMillis
            return moods
        }
    }

    private class FakeDeleteMoodUseCase : DeleteMoodUseCase {
        val deletedIds = mutableListOf<Int>()

        override suspend fun invoke(id: Int) {
            deletedIds += id
        }
    }
}
