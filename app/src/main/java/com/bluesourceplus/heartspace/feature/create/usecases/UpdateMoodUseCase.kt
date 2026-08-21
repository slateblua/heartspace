package com.bluesourceplus.heartspace.feature.create.usecases

import com.bluesourceplus.heartspace.data.MoodModel

interface UpdateMoodUseCase {
    suspend operator fun invoke(mood: MoodModel)
}