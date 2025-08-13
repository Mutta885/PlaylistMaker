package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistViewHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
    private val binding = PlaylistBinding.bind(parentView)
    fun bind(playlist: Playlist) = binding.apply {

        with(playlist) {
            tvName.text = name
            tvNumber.text = binding.root.resources.getQuantityString(R.plurals.tracks, playlist.tracksNumber, playlist.tracksNumber)
            val cover = if (coverPath.isNullOrEmpty()) R.drawable.placeholder
            else coverPath

            Glide.with(parentView)
                .load(cover)
                .into(ivCover as ImageView)
        }
    }


}