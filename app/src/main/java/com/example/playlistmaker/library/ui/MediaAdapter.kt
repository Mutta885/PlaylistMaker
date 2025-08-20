package com.example.playlistmaker.library.ui
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class MediaAdapter(
    private val onTrackClickDebounce: (track: Track) -> Unit,
    private val onLongClickListener: (track: Track) -> Boolean
) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    private var trackList: ArrayList<Track> = arrayListOf()


    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
        private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
        private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
        private val artworkUrlView: ImageView = itemView.findViewById(R.id.album_image)

        fun bind(item: Track) {
            trackNameView.text = item.trackName
            artistNameView.text = item.artistName
            trackTimeView.text = item.trackTime// SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime)
            artistNameView.requestLayout()
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_radius)))
                .dontAnimate()
                .into(artworkUrlView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_view, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val track = trackList[position]
        holder.apply {
            bind(track)
            itemView.setOnClickListener { onTrackClickDebounce(track) }
            itemView.setOnLongClickListener { onLongClickListener(track) }
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(trackList: ArrayList<Track>) {
        this.trackList = trackList
        notifyDataSetChanged()
    }

    fun clearList() {
        trackList.clear()
    }

}