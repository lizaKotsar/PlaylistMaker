package com.example.playlistmaker.ui.media.adapter


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
import com.example.playlistmaker.domain.playlists.model.Playlist

class PlaylistsAdapter(
    private val onClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistsAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return VH(view, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        private val onClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cover: ImageView = itemView.findViewById(R.id.ivCover)
        private val name: TextView = itemView.findViewById(R.id.tvName)
        private val count: TextView = itemView.findViewById(R.id.tvCount)

        fun bind(item: Playlist) {
            itemView.setOnClickListener { onClick(item) }

            name.text = item.name
            count.text = tracksCountText(item.tracksCount)

            Glide.with(itemView)
                .load(item.coverPath)
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .into(cover)
        }

        private fun tracksCountText(n: Int): String = when {
            n % 10 == 1 && n % 100 != 11 -> "$n трек"
            n % 10 in 2..4 && (n % 100 !in 12..14) -> "$n трека"
            else -> "$n треков"
        }
    }
}