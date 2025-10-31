package com.example.playlistmaker.ui.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import java.io.File

class BottomSheetPlaylistsAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistsAdapter.VH>() {

    private val items = mutableListOf<Playlist>()

    fun submit(list: List<Playlist>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlaylistBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    class VH(
        private val b: ItemPlaylistBottomSheetBinding,
        val onClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(b.root) {

        fun bind(item: Playlist) {
            b.tvName.text = item.name
            b.tvCount.text = b.root.context.resources.getQuantityString(
                R.plurals.tracks_count, item.tracksCount, item.tracksCount
            )

            val path = item.coverPath
            val req = if (path.isNullOrBlank()) null else File(path)
            Glide.with(b.ivCover)
                .load(req ?: R.drawable.ic_placeholder)
                .centerCrop()
                .into(b.ivCover)

            itemView.setOnClickListener { onClick(item) }
        }
    }
}