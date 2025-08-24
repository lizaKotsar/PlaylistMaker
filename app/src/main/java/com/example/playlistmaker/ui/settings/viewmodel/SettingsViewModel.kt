package com.example.playlistmaker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val isDarkLiveData = MutableLiveData<Boolean>()
    fun observeIsDark(): LiveData<Boolean> = isDarkLiveData


    private val _shareText = MutableLiveData<String>()
    val shareText: LiveData<String> = _shareText

    private val _openUrl = MutableLiveData<String>()
    val openUrl: LiveData<String> = _openUrl

    private val _sendEmail = MutableLiveData<EmailData>()
    val sendEmail: LiveData<EmailData> = _sendEmail

    init {
        isDarkLiveData.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun onThemeToggled(enabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(enabled)
        isDarkLiveData.postValue(enabled)
    }

    fun onShareAppClicked() {
        _shareText.value = sharingInteractor.getShareAppText()
    }

    fun onOpenTermsClicked() {
        _openUrl.value = sharingInteractor.getTermsUrl()
    }

    fun onOpenSupportClicked() {
        _sendEmail.value = sharingInteractor.getSupportEmailData()
    }
}
