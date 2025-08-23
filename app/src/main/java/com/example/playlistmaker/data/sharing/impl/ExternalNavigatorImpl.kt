package com.example.playlistmaker.data.sharing.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,

    private val shareAppLink: String,
    private val termsLink: String,
    private val supportEmail: String,
    private val supportSubject: String,
    private val supportBody: String,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(shareAppLink)
    }

    override fun openTerms() {
        externalNavigator.openLink(termsLink)
    }

    override fun openSupport() {
        externalNavigator.openEmail(
            EmailData(
                email = supportEmail,
                subject = supportSubject,
                body = supportBody
            )
        )
    }
}