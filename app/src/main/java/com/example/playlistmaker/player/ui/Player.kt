package com.example.playlistmaker.player.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.ui.PlaylistAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.PlaylistsState

class Player : Fragment() {

    private val viewModel by viewModel<PlayerViewModel>()

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private lateinit var currentTrack : Track
    private val args : PlayerArgs by navArgs()

    private var _adapter : PlaylistAdapter? = null
    private val adapter : PlaylistAdapter get() = _adapter!!

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = Gson().fromJson(args.currentTrack, Track::class.java)

        viewModel.preparePlayer(currentTrack)



        _adapter = PlaylistAdapter(
            viewType = PlaylistAdapter.FROM_PLAYER,
            onItemClick = { playlist ->
                viewModel.addTrackToPlaylist(playlist)
            })
        binding.recyclerView.adapter = _adapter

        setBottomSheetBehavior()

        viewModel.getPlaylists()

        viewModel.playerStateLiveData.observe(viewLifecycleOwner) { playerState ->
            when (playerState) {
                is PlayerState.Prepared -> {
                    //val currentTrack = playerState.track
                    val albumTemp = currentTrack.collectionName
                    viewModel.isFavoriteState(currentTrack.trackId)
                    binding.trackTimeValue.text = currentTrack.trackTime// SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)

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
                    val bigCover = currentTrack.artworkUrl100  //.replaceAfterLast('/',"512x512bb.jpg")

                    Glide.with(requireContext())
                        .load(bigCover)
                        .placeholder(R.drawable.album_placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.value_8)))
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
        viewModel.isFavoriteState(currentTrack.trackId)


        viewModel.PlaylistsLiveData.observe(viewLifecycleOwner) { state ->
                managePlaylists(state)
            }
        viewModel.TrackAddedLiveData.observe(viewLifecycleOwner) { message ->
                showMessage(message)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }

        viewModel.isFavoriteLiveData.observe(viewLifecycleOwner) { isFavorite ->
            turnFavorite(isFavorite)
        }



        binding.toFavoriteButton.setOnClickListener(){
            viewModel.swithFavorite()
        }


        binding.menuButton.setOnClickListener(){
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener(){
            viewModel.onPlayButtonClicked()
        }

        binding.toPlayListButton.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.btCreatePlaylist.setOnClickListener {
            findNavController().navigate(PlayerDirections.actionPlayerFragmentToMakerFragment(playlist = null))
        }

    }

    private fun turnFavorite(isFavorite: Boolean) {
        binding.toFavoriteButton.apply {
            if (isFavorite)
                setImageResource(R.drawable.to_favorite_true)
            else
                setImageResource(R.drawable.to_favorite)
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

        Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show()

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

    override fun onDestroyView() {
        _binding = null
        _adapter = null
        super.onDestroyView()
    }


}