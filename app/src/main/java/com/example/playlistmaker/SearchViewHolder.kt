package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrlView: ImageView = itemView.findViewById(R.id.album_image)

    fun bind(item: Track) {
        trackNameView.text = item.trackName
        artistNameView.text = item.artistName
        trackTimeView.text = item.trackTime

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_radius)))
            .dontAnimate()
            .into(artworkUrlView)
    }

}


