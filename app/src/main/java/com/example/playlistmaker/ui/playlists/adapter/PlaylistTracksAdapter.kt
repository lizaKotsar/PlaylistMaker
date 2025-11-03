package com.example.playlistmaker.ui.playlists.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistTracksAdapter(
    private val onClick: (Track) -> Unit,
    private val onLongClick: (Track) -> Unit
) : ListAdapter<Track, PlaylistTracksAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) =
            oldItem.trackId == newItem.trackId

        override fun areContentsTheSame(oldItem: Track, newItem: Track) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return VH(v, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    class VH(
        itemView: View,
        private val onClick: (Track) -> Unit,
        private val onLongClick: (Track) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cover: ImageView = itemView.findViewById(R.id.track_artwork)
        private val name: TextView = itemView.findViewById(R.id.track_name)
        private val artist: TextView = itemView.findViewById(R.id.track_artist)
        private val dot: TextView = itemView.findViewById(R.id.track_dot)
        private val duration: TextView = itemView.findViewById(R.id.track_duration)

        private val mmss = SimpleDateFormat("mm:ss", Locale.getDefault())

        fun bind(t: Track) {
            name.text = t.trackName.orEmpty()
            artist.text = t.artistName.orEmpty()
            duration.text = mmss.format((t.trackTimeMillis ?: 0L))

            Glide.with(itemView)
                .load(t.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .into(cover)


            itemView.setOnClickListener { onClick(t) }
            itemView.setOnLongClickListener {
                onLongClick(t)
                true
            }

            dot.visibility = View.VISIBLE
        }
    }
}