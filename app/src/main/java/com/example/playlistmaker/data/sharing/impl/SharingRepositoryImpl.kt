package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.model.SharingRepository

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {

    override fun getShareAppText(): String =
        context.getString(R.string.share_text)

    override fun getTermsUrl(): String =
        context.getString(R.string.user_agreement_url)

    override fun getSupportEmailData(): EmailData =
        EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_tema),
            body = context.getString(R.string.support_message)
        )
}
