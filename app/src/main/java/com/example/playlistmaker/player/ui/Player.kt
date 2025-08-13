package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.library.ui.PlaylistAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.PlaylistsState

class Player : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var currentTrack : Track
    private lateinit var adapter: PlaylistAdapter
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = Gson().fromJson(intent.extras?.getString("TRACK"), Track::class.java)

        adapter = PlaylistAdapter(
            viewType = PlaylistAdapter.FROM_PLAYER,
            onItemClick = { playlist ->
                viewModel.addTrackToPlaylist(playlist)
            })
        binding.recyclerView.adapter = adapter

        setBottomSheetBehavior()

        viewModel.preparePlayer(currentTrack)



        viewModel.getPlaylists()




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




        viewModel.PlaylistsLiveData.observe(this) { state ->
                managePlaylists(state)
            }
        viewModel.TrackAddedLiveData.observe(this) { message ->
                showMessage(message)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
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

        binding.toPlayListButton.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding.btCreatePlaylist.setOnClickListener {
            findNavController(this, R.id.maker).navigate(R.id.action_playerFragment_to_makerFragment)
        }

    }





    private fun managePlaylists(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showPlaylists(state.playlists)
        }
    }


    private fun showPlaceholder() {
        binding.apply {
            recyclerView.isVisible = false
            progressbar.isVisible = false
            layoutPlaceholder.isVisible = true
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.apply {
            recyclerView.isVisible = true
            progressbar.isVisible = false
            layoutPlaceholder.isVisible = false
        }
        adapter?.clearList()
        adapter?.submitList(playlists as ArrayList)
        adapter?.notifyDataSetChanged()
    }

    private fun showMessage(message: String) {

        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()

    }

    private fun setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutToPlaylist).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val overlay = binding.overlay

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }

                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

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