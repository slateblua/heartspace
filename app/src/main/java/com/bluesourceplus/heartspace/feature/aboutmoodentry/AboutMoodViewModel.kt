package com.bluesourceplus.heartspace.feature.aboutmoodentry

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluesourceplus.heartspace.data.Mood
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.DeleteMoodUseCase
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.GetMoodByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface AboutMoodState {
    data object Missing : AboutMoodState

    data class Content(
        val id: Int = 0,
        val note: String = "",
        val mood: Mood = Mood.SAD,
    ) : AboutMoodState
}

@Immutable
sealed class AboutMoodEffect {
    data object MoodDeleted : AboutMoodEffect()
}

@Immutable
sealed interface AboutMoodIntent {
    data class LoadMood(
        val moodId: Int,
    ) : AboutMoodIntent

    data class DeleteMood(
        val moodId: Int,
    ) : AboutMoodIntent
}


class AboutMoodViewModel(
    private val getMoodByIdUseCase: GetMoodByIdUseCase,
    private val deleteMoodUseCase: DeleteMoodUseCase,
) : ViewModel() {
    private val _state =
        MutableStateFlow<AboutMoodState>(
            AboutMoodState.Content()
        )
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<AboutMoodEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun handleEvent(event: AboutMoodIntent) {
        when (event) {
            is AboutMoodIntent.LoadMood -> loadMood(event.moodId)
            is AboutMoodIntent.DeleteMood -> deleteMood(moodId = event.moodId)
        }
    }

    private fun deleteMood(moodId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteMoodUseCase(moodId)
            _sideEffect.send(AboutMoodEffect.MoodDeleted)
        }
    }

    private fun loadMood(moodId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val moodModel = getMoodByIdUseCase(moodId).first()
            _state.update {
                if (moodModel == null) {
                    AboutMoodState.Missing
                } else {
                    AboutMoodState.Content(
                        id = moodId,
                        note = moodModel.note,
                        mood = moodModel.mood,
                    )
                }
            }
        }
    }
}