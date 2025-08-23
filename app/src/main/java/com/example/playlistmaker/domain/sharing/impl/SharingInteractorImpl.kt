package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String =
        "https://practicum.yandex.ru"

    private fun getTermsLink(): String =
        "https://yandex.ru/legal/practicum_termsofuse/"

    private fun getSupportEmailData(): EmailData =
        EmailData(
            email = "yelizaveta.kotsar@bk.ru",
            subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            body = "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
}