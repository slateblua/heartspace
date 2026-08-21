package com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases

import com.bluesourceplus.heartspace.data.MoodRepo

class DeleteMoodUseCaseImpl(private val moodRepo: MoodRepo) : DeleteMoodUseCase {
    override suspend fun invoke(id: Int) {
        moodRepo.deleteById(id)
    }
}
