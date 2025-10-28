package com.example.playlistmaker.ui.media.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import java.io.File

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.VH>() {

    private val items = mutableListOf<Playlist>()

    fun submit(list: List<Playlist>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class VH(private val b: ItemPlaylistBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Playlist) {
            b.tvName.text = item.name
            b.tvCount.text = formatCount(itemView, item.tracksCount)

            if (item.coverPath.isNullOrBlank()) {
                b.ivCover.setImageResource(R.drawable.ic_placeholder)
            } else {
                Glide.with(b.ivCover)
                    .load(File(item.coverPath!!))
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(b.ivCover)
            }
        }

        private fun formatCount(v: android.view.View, count: Int): String {
            return v.resources.getQuantityString(R.plurals.tracks_count, count, count)
        }
    }
}