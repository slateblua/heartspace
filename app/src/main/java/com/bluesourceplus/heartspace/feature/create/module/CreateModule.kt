package com.bluesourceplus.heartspace.feature.create.module

import com.bluesourceplus.heartspace.feature.create.CreateMoodMode
import com.bluesourceplus.heartspace.feature.create.CreateViewModel
import com.bluesourceplus.heartspace.feature.create.usecases.AddMoodUseCase
import com.bluesourceplus.heartspace.feature.create.usecases.AddMoodUseCaseImpl
import com.bluesourceplus.heartspace.feature.create.usecases.UpdateMoodUseCase
import com.bluesourceplus.heartspace.feature.create.usecases.UpdateMoodUseCaseImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val createModule = module {
    viewModel { (mode: CreateMoodMode) -> CreateViewModel(mode, get(), get(), get()) }

    single { AddMoodUseCaseImpl(get()) } bind AddMoodUseCase::class

    single { UpdateMoodUseCaseImpl(get()) } bind UpdateMoodUseCase::class
}
