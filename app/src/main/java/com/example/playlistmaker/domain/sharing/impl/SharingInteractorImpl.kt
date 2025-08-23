package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import androidx.core.content.contentValuesOf
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context,
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
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_tema),
            body = context.getString(R.string.support_message)
        )
}