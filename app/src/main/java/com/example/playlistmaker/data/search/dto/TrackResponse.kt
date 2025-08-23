package com.example.playlistmaker.data.search.dto


import com.google.gson.annotations.SerializedName
//sprint16
data class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
) : Response()
