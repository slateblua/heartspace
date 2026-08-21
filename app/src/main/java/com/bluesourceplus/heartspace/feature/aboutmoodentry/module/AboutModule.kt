package com.bluesourceplus.heartspace.feature.aboutmoodentry.module

import com.bluesourceplus.heartspace.feature.aboutmoodentry.AboutMoodViewModel
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.DeleteMoodUseCase
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.DeleteMoodUseCaseImpl
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.GetMoodByIdUseCase
import com.bluesourceplus.heartspace.feature.aboutmoodentry.usecases.GetMoodByIdUseCaseImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val aboutModule = module {
    viewModel { AboutMoodViewModel(get(), get()) }

    single { GetMoodByIdUseCaseImpl(get()) } bind GetMoodByIdUseCase::class

    single { DeleteMoodUseCaseImpl(get()) } bind DeleteMoodUseCase::class
}
