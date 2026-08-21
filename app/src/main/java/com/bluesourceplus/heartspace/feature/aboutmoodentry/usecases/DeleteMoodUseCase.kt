package com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases

interface DeleteMoodUseCase {
    suspend operator fun invoke(id: Int)
}
