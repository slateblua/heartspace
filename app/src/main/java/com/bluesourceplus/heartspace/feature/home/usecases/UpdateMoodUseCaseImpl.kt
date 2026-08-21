package com.bluesourceplus.heartspace.feature.home.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.data.MoodRepo

class UpdateMoodUseCaseImpl(private val moodRepo: MoodRepo) : UpdateMoodUseCase {
    override suspend fun invoke(moodModel: MoodModel) {
        moodRepo.update(moodModel)
    }
}