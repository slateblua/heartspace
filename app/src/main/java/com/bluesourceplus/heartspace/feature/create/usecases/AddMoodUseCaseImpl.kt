package com.bluesourceplus.heartspace.feature.create.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.data.MoodRepo

class AddMoodUseCaseImpl (private val moodRepo: MoodRepo) : AddMoodUseCase {
    override suspend operator fun invoke(moodModel: MoodModel) {
        moodRepo.add(moodModel)
    }
}