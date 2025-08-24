package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {
    fun getShareAppText(): String
    fun getTermsUrl(): String
    fun getSupportEmailData(): EmailData
}