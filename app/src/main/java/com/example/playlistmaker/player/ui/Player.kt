package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.util.Locale

class Player : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.preparePlayer()

        viewModel.playerStateLiveData.observe(this) { playerState ->
            when (playerState) {
                is PlayerState.Loading -> {
                    val currentTrack = playerState.track
                    val albumTemp = currentTrack.collectionName
                    binding.trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)

                    if (albumTemp.isEmpty()){
                        binding.albumValue.visibility = View.GONE
                        binding.albumTitle.visibility = View.GONE
                    }
                    else {
                        binding.albumValue.visibility = View.VISIBLE
                        binding.albumTitle.visibility = View.VISIBLE
                        binding.albumValue.text = albumTemp
                    }
                    binding.timer.text = "00:00"
                    binding.yearValue.text = currentTrack.releaseDate.take(4)
                    binding.genreValue.text = currentTrack.primaryGenreName
                    binding.countryValue.text = currentTrack.country
                    binding.trackName.text = currentTrack.trackName
                    binding.artistName.text = currentTrack.artistName
                    val bigCover = currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

                    Glide.with(applicationContext)
                        .load(bigCover)
                        .placeholder(R.drawable.album_placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.value_8)))
                        .dontAnimate()
                        .into(binding.cover)

                }

                is PlayerState.Paused -> {
                    binding.timer.text = playerState.timer
                    binding.playButton.setImageResource(R.drawable.play)
                }
                is PlayerState.Playing -> {
                    binding.timer.text = playerState.timer
                    binding.playButton.setImageResource(R.drawable.pause)
                }
                is PlayerState.Prepared -> {
                    binding.timer.text = playerState.timer
                    binding.playButton.setImageResource(R.drawable.play)

                }
            }
        }




        binding.menuButton.setOnClickListener(){
            finish()
        }

        binding.playButton.setOnClickListener(){
            viewModel.playbackControl()
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }


}