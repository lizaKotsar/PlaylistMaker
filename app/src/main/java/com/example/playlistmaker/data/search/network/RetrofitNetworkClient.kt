package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackResponse
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {


    private val baseUrl = "https://itunes.apple.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: ITunesApi = retrofit.create(ITunesApi::class.java)

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