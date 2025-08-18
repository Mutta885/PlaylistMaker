package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistAdapter(private val viewType: Int, private val onItemClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    private var playlists: ArrayList<Playlist> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layout = if (this.viewType == FROM_PLAYLIST) {
            R.layout.playlist
        } else {
            R.layout.playlist_player
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick(playlist) }

    }


    fun submitList(playlists: ArrayList<Playlist>) {
        this.playlists = playlists
    }

    fun clearList() {
        playlists.clear()
    }


    companion object {
        const val FROM_PLAYLIST = 1
        const val FROM_PLAYER = 2

    }
}