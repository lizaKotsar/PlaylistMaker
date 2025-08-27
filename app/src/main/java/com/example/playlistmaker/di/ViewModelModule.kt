package com.example.playlistmaker.di

import com.example.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {


    viewModel { SearchViewModel(get(), get()) }


    viewModel { PlayerViewModel(get()) }


    viewModel { SettingsViewModel(get(), get()) }
}