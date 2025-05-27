package com.example.playlistmaker.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackInfo: TextView = itemView.findViewById(R.id.track_info)
    private val trackArtwork: ImageView = itemView.findViewById(R.id.track_artwork)

    fun bind(track: Track) {
        trackName.text = track.trackName.orEmpty()

        val time = track.trackTimeMillis ?: 0L
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
        val artist = track.artistName.orEmpty()
        trackInfo.text = "$artist · $formattedTime"

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .into(trackArtwork)
    }
}