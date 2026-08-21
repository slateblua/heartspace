package com.bluesourceplus.heartspace.feature.home.usecases

import com.bluesourceplus.heartspace.data.MoodModel
import kotlinx.coroutines.flow.Flow

interface GetMoodsForDateUseCase {
    operator fun invoke(startMillis: Long, endMillis: Long): Flow<List<MoodModel>>
}
