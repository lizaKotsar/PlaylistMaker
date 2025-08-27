package com.example.playlistmaker.di

import com.example.playlistmaker.common.AndroidResourceProvider
import com.example.playlistmaker.common.ResourceProvider
import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {


    single<ResourceProvider> { AndroidResourceProvider(androidContext()) }


    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
}