package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackResponse
import com.example.playlistmaker.data.search.dto.TracksSearchRequest



class RetrofitNetworkClient(
    private val api: ITunesApi
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return when (dto) {
            is TracksSearchRequest -> {
                val resp = api.search(dto.expression).execute()
                val body = resp.body() ?: TrackResponse(resultCount = 0, results = emptyList())
                body.apply { resultCode = resp.code() }
            }
            else -> Response().apply { resultCode = 400 }
        }
    }
}