package com.example.playlistmaker.common



import android.content.Context
import androidx.annotation.StringRes


interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
}


class AndroidResourceProvider(
    private val context: Context
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)
}