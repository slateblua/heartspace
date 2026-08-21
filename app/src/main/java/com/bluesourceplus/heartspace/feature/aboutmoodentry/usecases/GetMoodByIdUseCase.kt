package com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import kotlinx.coroutines.flow.Flow

interface GetMoodByIdUseCase {
    operator fun invoke(id: Int): Flow<MoodModel?>
}
