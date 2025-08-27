package com.example.playlistmaker.domain.sharing.model

interface SharingRepository {
    fun getShareAppText(): String
    fun getTermsUrl(): String
    fun getSupportEmailData(): EmailData
}