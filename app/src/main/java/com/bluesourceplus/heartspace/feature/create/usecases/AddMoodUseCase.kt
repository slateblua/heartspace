package com.bluesourceplus.heartspace.feature.create.usecases

import com.bluesourceplus.heartspace.data.MoodModel

interface AddMoodUseCase {
    suspend operator fun invoke(moodModel: MoodModel)
}