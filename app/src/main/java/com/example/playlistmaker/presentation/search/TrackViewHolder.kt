package com.example.playlistmaker.presentation.search

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.track_artist)
    private val durationText: TextView = itemView.findViewById(R.id.track_duration)
    private val dotView: TextView = itemView.findViewById(R.id.track_dot)
    private val trackArtwork: ImageView = itemView.findViewById(R.id.track_artwork)
    private val container: View = itemView.findViewById(R.id.track_info_container)

    fun bind(track: Track) {
        val name = track.trackName.orEmpty()
        val artist = track.artistName.orEmpty()
        val time = track.trackTimeMillis ?: 0L
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

        trackName.text = name
        durationText.text = formattedTime

        container.post {
            val availableWidth = container.width
            val durationWidth = durationText.paint.measureText(formattedTime)
            val dotWidth = dotView.paint.measureText(" • ")
            val density = itemView.context.resources.displayMetrics.density
            val reservedWidth = durationWidth + dotWidth + 16 * density
            val maxArtistWidth = availableWidth - reservedWidth

            val ellipsized = TextUtils.ellipsize(
                artist,
                artistName.paint,
                maxArtistWidth,
                TextUtils.TruncateAt.END
            )

            artistName.text = ellipsized
        }

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .into(trackArtwork)
    }
}