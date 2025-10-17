package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.Response

import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val api: ITunesApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return when (dto) {
            is TracksSearchRequest -> withContext(Dispatchers.IO) {
                try {
                    val body = api.search(dto.expression)
                    body.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    Response().apply { resultCode = 500 }
                }
            }
            else -> Response().apply { resultCode = 400 }
        }
    }
}