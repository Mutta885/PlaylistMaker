package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class Player : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var currentTrack : Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = Gson().fromJson(intent.extras?.getString("TRACK"), Track::class.java)

        viewModel.preparePlayer(currentTrack)

        viewModel.playerStateLiveData.observe(this) { playerState ->
            when (playerState) {
                is PlayerState.Prepared -> {
                    //val currentTrack = playerState.track
                    val albumTemp = currentTrack.collectionName
                    viewModel.isFavoriteState(currentTrack.trackId)
                    binding.trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)

                    if (albumTemp.isEmpty()){
                        binding.albumValue.isVisible = false
                        binding.albumTitle.isVisible = false
                    }
                    else {
                        binding.albumValue.isVisible = true
                        binding.albumTitle.isVisible = true
                    }
                    binding.timer.text = "00:00"
                    binding.yearValue.text = currentTrack.releaseDate.take(4)
                    binding.genreValue.text = currentTrack.primaryGenreName
                    binding.countryValue.text = currentTrack.country
                    binding.trackName.text = currentTrack.trackName
                    binding.artistName.text = currentTrack.artistName
                    binding.albumValue.text = currentTrack.collectionName
                    val bigCover = currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

                    Glide.with(applicationContext)
                        .load(bigCover)
                        .placeholder(R.drawable.album_placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.value_8)))
                        .dontAnimate()
                        .into(binding.cover)

                    binding.playButton.setImageResource(R.drawable.play)
                }

                is PlayerState.Paused -> {
                    binding.timer.text = playerState.progress
                    binding.playButton.setImageResource(R.drawable.play)
                }
                is PlayerState.Playing -> {
                    binding.timer.text = playerState.progress
                    binding.playButton.setImageResource(R.drawable.pause)
                }
                else -> {}

            }
        }

        viewModel.isFavoriteLiveData.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.toFavoriteButton.setImageResource(R.drawable.to_favorite_true)
            } else {
                binding.toFavoriteButton.setImageResource(R.drawable.to_favorite)
            }
        }

        binding.toFavoriteButton.setOnClickListener(){
            viewModel.swithFavorite(currentTrack)
        }


        binding.menuButton.setOnClickListener(){
            finish()
        }

        binding.playButton.setOnClickListener(){
            viewModel.onPlayButtonClicked()
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