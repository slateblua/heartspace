package com.bluesourceplus.heartspace.feature.home.usecases

import com.bluesourceplus.heartspace.data.MoodModel

interface UpdateMoodUseCase {
    suspend operator fun invoke(moodModel: MoodModel)
}