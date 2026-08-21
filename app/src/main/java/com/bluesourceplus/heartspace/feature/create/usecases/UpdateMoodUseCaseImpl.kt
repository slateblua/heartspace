package com.bluesourceplus.heartspace.feature.create.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.data.MoodRepo

class UpdateMoodUseCaseImpl(private val moodRepo: MoodRepo) : UpdateMoodUseCase {
    override suspend fun invoke(mood: MoodModel) {
        moodRepo.update(mood)
    }
}