package com.example.playlistmaker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val isDarkLiveData = MutableLiveData<Boolean>()

    fun observeIsDark(): LiveData<Boolean> = isDarkLiveData

    init {

        isDarkLiveData.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun onThemeToggled(enabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(enabled)
        isDarkLiveData.postValue(enabled)
    }

    fun shareApp() = sharingInteractor.shareApp()
    fun openTerms() = sharingInteractor.openTerms()
    fun openSupport() = sharingInteractor.openSupport()
}