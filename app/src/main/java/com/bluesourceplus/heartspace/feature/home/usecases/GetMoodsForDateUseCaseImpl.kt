package com.bluesourceplus.heartspace.feature.home.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import com.bluesourceplus.heartspace.data.MoodRepo
import kotlinx.coroutines.flow.Flow

class GetMoodsForDateUseCaseImpl(private val moodRepo: MoodRepo) : GetMoodsForDateUseCase {
    override fun invoke(startMillis: Long, endMillis: Long): Flow<List<MoodModel>> {
        return moodRepo.getMoodsForDate(startMillis, endMillis)
    }
}
