package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class Player : AppCompatActivity() {

    private lateinit var cover: ImageView
    private lateinit var trackTimeValue: TextView
    private lateinit var albumTitle: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var menu_button: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var timerValue: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        cover = findViewById(R.id.cover)
        menu_button = findViewById(R.id.menu_button)
        trackTimeValue = findViewById(R.id.trackTimeValue)
        albumTitle = findViewById(R.id.albumTitle)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        timerValue = findViewById(R.id.timer)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)


        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val currentTrack : Track = SearchHistory(sharedPrefs).historyList[0]

        menu_button.setOnClickListener(){
            finish()
        }

        timerValue.text = "0:00"
        trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)

        val albumTemp = currentTrack.collectionName
        if (albumTemp.isEmpty()){
            albumValue.visibility = View.GONE
            albumTitle.visibility = View.GONE
        }
        else {
            albumValue.visibility = View.VISIBLE
            albumTitle.visibility = View.VISIBLE
            albumValue.text = albumTemp
        }


        yearValue.text = currentTrack.releaseDate.take(4)
        genreValue.text = currentTrack.primaryGenreName
        countryValue.text = currentTrack.country

        trackName.text = currentTrack.trackName
        artistName.text = currentTrack.artistName

        val bigCover = currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

        Glide.with(applicationContext)
            .load(bigCover)
            .placeholder(R.drawable.album_placeholder)
            .fitCenter()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.value_8)))
            .dontAnimate()
            .into(cover)



    }



}