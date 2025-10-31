package com.example.playlistmaker.ui.media.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import java.io.File

class PlaylistsAdapter :
    ListAdapter<Playlist, PlaylistsAdapter.VH>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val b: ItemPlaylistBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Playlist) {
            b.tvName.text = item.name
            b.tvCount.text = formatCount(itemView, item.tracksCount)

            val path = item.coverPath
            if (path.isNullOrBlank()) {
                b.ivCover.setImageResource(R.drawable.ic_placeholder)
            } else {
                Glide.with(b.ivCover)
                    .load(File(path))
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(b.ivCover)
            }
        }

        private fun formatCount(v: View, count: Int): String {
            return v.resources.getQuantityString(R.plurals.tracks_count, count, count)
        }
    }
}