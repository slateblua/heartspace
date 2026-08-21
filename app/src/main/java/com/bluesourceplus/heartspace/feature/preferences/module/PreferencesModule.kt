package com.bluesourceplus.heartspace.feature.preferences.module

import com.bluesourceplus.heartspace.feature.preferences.PreferencesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val preferencesModule = module {
    viewModel { PreferencesViewModel() }
}