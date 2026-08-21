package com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.data.MoodRepo
import kotlinx.coroutines.flow.Flow

class GetMoodByIdUseCaseImpl(private val moodRepo: MoodRepo) : GetMoodByIdUseCase {
    override fun invoke(id: Int): Flow<MoodModel?> {
        return moodRepo.getById(id)
    }
}
