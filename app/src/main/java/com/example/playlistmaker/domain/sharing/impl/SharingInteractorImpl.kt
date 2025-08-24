package com.example.playlistmaker.domain.sharing.impl


import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.model.SharingRepository

class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun getShareAppText(): String = repository.getShareAppText()

    override fun getTermsUrl(): String = repository.getTermsUrl()

    override fun getSupportEmailData(): EmailData = repository.getSupportEmailData()
}