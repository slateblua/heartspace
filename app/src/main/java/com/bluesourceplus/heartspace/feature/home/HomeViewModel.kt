package com.bluesourceplus.heartspace.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.DeleteMoodUseCase
import com.bluesourceplus.heartspace.feature.home.usecases.GetMoodsForDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Immutable
data class HomeScreenState(
    val selectedDate: LocalDate,
    val moods: List<MoodModel>,
    val canGoNext: Boolean,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getMoodsForDateUseCase: GetMoodsForDateUseCase,
    private val deleteMoodUseCase: DeleteMoodUseCase,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val now: () -> Instant = { Clock.System.now() },
) : ViewModel() {
    private val selectedDate = MutableStateFlow(today())

    fun handleEvent(event: HomeScreenIntent) {
        when (event) {
            is HomeScreenIntent.DeleteMood -> deleteMood(event.moodId)
            HomeScreenIntent.GoToNextDay -> goToNextDay()
            HomeScreenIntent.GoToPreviousDay -> goToPreviousDay()
            HomeScreenIntent.GoToToday -> selectedDate.value = today()
        }
    }

    private fun deleteMood(moodId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteMoodUseCase(moodId)
        }
    }

    val state: StateFlow<HomeScreenState> =
        selectedDate
            .flatMapLatest { date ->
                val startMillis = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
                val endMillis = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds()

                getMoodsForDateUseCase(startMillis, endMillis).map { moods ->
                    date to moods
                }
            }.map { dateAndMoods ->
                val date = dateAndMoods.first
                val moods = dateAndMoods.second
                HomeScreenState(
                    selectedDate = date,
                    moods = moods,
                    canGoNext = date < today(),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeScreenState(
                    selectedDate = selectedDate.value,
                    moods = emptyList(),
                    canGoNext = false,
                ),
            )

    private fun goToPreviousDay() {
        selectedDate.update { it.minus(1, DateTimeUnit.DAY) }
    }

    private fun goToNextDay() {
        selectedDate.update { date ->
            if (date < today()) {
                date.plus(1, DateTimeUnit.DAY)
            } else {
                date
            }
        }
    }

    private fun today(): LocalDate {
        return now().toLocalDateTime(timeZone).date
    }
}

@Immutable
sealed interface HomeScreenIntent {
    data class DeleteMood(
        val moodId: Int,
    ) : HomeScreenIntent

    data object GoToPreviousDay : HomeScreenIntent

    data object GoToNextDay : HomeScreenIntent

    data object GoToToday : HomeScreenIntent
}
