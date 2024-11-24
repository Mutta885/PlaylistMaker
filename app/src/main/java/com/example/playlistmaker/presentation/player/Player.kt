package com.example.playlistmaker.presentation.player

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.PlayerInteractor
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
    private lateinit var playButton: ImageButton

    private lateinit var playerInteractor: PlayerInteractor
    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        mainThreadHandler = Handler(Looper.getMainLooper())

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

        playerInteractor = Creator.providePlayerInteractor()
        preparePlayer()
        val currentTrack= playerInteractor.getCrrntTrack()
        val albumTemp = currentTrack.collectionName

        menu_button.setOnClickListener(){
            finish()
        }

        timerValue.text = "00:00"
        trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)

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

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacksAndMessages(null)
        playerInteractor.destroyPlayer()
        super.onDestroy()
    }

    private fun preparePlayer() {
        playButton = findViewById(R.id.playButton)
        playerInteractor.preparePlayer {
            mainThreadHandler?.removeCallbacksAndMessages(null)
            timerValue.text = "00:00"
            playButton.setImageResource(R.drawable.play)
        }

        playButton.setOnClickListener(){
            playerInteractor.playbackControl()
            timerControl()
            when (playerInteractor.getPlayBttnState()) {
                true -> playButton.setImageResource(R.drawable.play)
                else -> playButton.setImageResource(R.drawable.pause)
            }
        }
    }

    private fun timerControl(){
        when(playerInteractor.getPlayerState()) {
            STATE_PLAYING -> {
                mainThreadHandler?.post(createUpdateTimerTask())
            }
            STATE_PAUSED -> {
                mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
            }

        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                timerValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getMediaPlayerTimer())
                mainThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }

}