package com.bluesourceplus.heartspace.feature.home.module

import com.bluesourceplus.heartspace.feature.home.HomeViewModel
import com.bluesourceplus.heartspace.feature.home.usecases.GetMoodsForDateUseCase
import com.bluesourceplus.heartspace.feature.home.usecases.GetMoodsForDateUseCaseImpl
import com.bluesourceplus.heartspace.feature.home.usecases.UpdateMoodUseCase
import com.bluesourceplus.heartspace.feature.home.usecases.UpdateMoodUseCaseImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get(), get()) }

    single { GetMoodsForDateUseCaseImpl(get()) } bind GetMoodsForDateUseCase::class

    single { UpdateMoodUseCaseImpl(get()) } bind UpdateMoodUseCase::class
}
