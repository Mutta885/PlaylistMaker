package com.example.playlistmaker.library.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.playlistmaker.databinding.FragmentPlaylistScreenBinding
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.widget.LinearLayout
import androidx.core.view.isGone
import com.example.playlistmaker.library.domain.models.Playlist
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.example.playlistmaker.R
import com.bumptech.glide.Glide
import androidx.core.view.isVisible


class PlaylistScreen : Fragment() {

    private var _binding: FragmentPlaylistScreenBinding? = null
    private val binding: FragmentPlaylistScreenBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    private var _playlistId: Int? = null
    private val playlistId: Int get() = requireNotNull(_playlistId)

    private var _adapter: MediaAdapter? = null
    private val adapter: MediaAdapter get() = requireNotNull(_adapter) { "MediaAdapter wasn't initialized" }

    private val args by navArgs <PlaylistScreenArgs>()
    private val viewModel by viewModel <PlaylistScreenViewModel>()

    private var trackListBSBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var moreMenuBSBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var descriptionIsExpanded = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _playlistId = args.playlistId

        val onTrackClickDebounce: (Track) -> Unit = { track ->
            showPlayer(track)
        }

        val onLongClickListener: (Track) -> Boolean = { track ->
            showDeleteTrackDialog(track.trackId)
            true
        }


        _adapter = MediaAdapter(onTrackClickDebounce, onLongClickListener)

        setBottomSheetsBehavior()

        binding.recyclerView.adapter = adapter

        setCLickListeners()

        with(viewModel) {
            updatePlaylist(playlistId)

            getPlaylistLiveData().observe(viewLifecycleOwner) { playlist ->
                fillPlaylistData(playlist)
            }
            getTracksLiveData().observe(viewLifecycleOwner) {
                render(it)
            }
            getNavigateUpEvent.observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }
        }

    }


    private fun fillPlaylistData(playlist: Playlist) {
        binding.apply {
            with(playlist) {
                playlistName.text = title
                if (!description.isNullOrEmpty()) {
                    tdescription.isVisible = true
                    tdescription.text = description
                } else {
                    tdescription.isVisible = false
                }
                Glide.with(requireContext())
                    .load(coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.placeholderpl)
                    .into(cover)

                with(playlistPlayer) {
                    Glide.with(requireContext())
                        .load(coverPath)
                        .centerCrop()
                        .placeholder(R.drawable.placeholderpl)
                        .into(ivCover)

                    tvName.text = title
                }

            }
        }
    }

    private fun showDeleteTrackDialog(trackId: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.do_you_want_to_delete_track))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.removeTrackFromPlaylist(trackId)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }


    private fun render(state: PlaylistScreenViewModel.TracksState) {
        when (state) {
            is PlaylistScreenViewModel.TracksState.Content -> showContent(state.tracks)
            is PlaylistScreenViewModel.TracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            recyclerView.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            tduration.text = getString(R.string.zero_minutes)
            tvQuantity.text = viewModel.getQuantityText(0)
        }
    }

    private fun showContent(tracks: List<Track>) {
        val quantityText = viewModel.getQuantityText(tracks.size)
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            tduration.text = viewModel.getTotalDurationText(tracks)
            tvQuantity.text = quantityText
            playlistPlayer.tvNumber.text = quantityText
            adapter.submitList(tracks as ArrayList<Track>)
        }
    }

    private fun showPlayer(track: Track) {
        findNavController().navigate(
            PlaylistScreenDirections.actionPlaylistToPlayer(Gson().toJson(track))
        )
    }

    private fun checkShareAction() {
        if (viewModel.getTracksLiveData().value is PlaylistScreenViewModel.TracksState.Empty)
            showMessageListIsEmpty()
        else sharePlaylist()
    }

    private fun sharePlaylist() {
        val state = viewModel.getTracksLiveData().value
        val playlist = viewModel.getPlaylistLiveData().value
        val context = requireContext()
        if ((state is PlaylistScreenViewModel.TracksState.Content) && (playlist != null)) {

            val message = buildString {
                appendLine(context.getString(R.string.playlist, playlist.title))
                playlist.description?.let { appendLine(it) }
                appendLine(viewModel.getQuantityText(playlist.tracksQuantity))
                appendLine()

                state.tracks.forEachIndexed { index, track ->
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
                }
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_playlist)
                )
            )
        }
    }

    private fun showMoreMenu() {
        moreMenuBSBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun setCLickListeners() {
        binding.apply {
            tvShare.setOnClickListener {
                checkShareAction()
                moreMenuBSBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            tvEdit.setOnClickListener {
                goToEditor()

            }
            tvDeletePlaylist.setOnClickListener {
                deletePlaylist()
            }

            arrowBack.setOnClickListener {
                findNavController().navigateUp()
            }

            ivShare.setOnClickListener {
                checkShareAction()
            }

            ivMenu.setOnClickListener {
                showMoreMenu()
            }
            tdescription.setOnClickListener {
                toggleExpandingDescription()
            }
        }
    }

    private fun goToEditor() {
        findNavController().navigate(
            PlaylistScreenDirections.actionPlaylistToMakerFragment(
                Gson().toJson(viewModel.getPlaylistLiveData().value)
            )
        )
    }

    private fun setBottomSheetsBehavior() {
        binding.apply {
            trackListBSBehavior = BottomSheetBehavior.from(tracksInPlaylist).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }

            moreMenuBSBehavior = BottomSheetBehavior.from(menuBottom).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

            val overlay = binding.overlay

            moreMenuBSBehavior?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    overlay.isGone = newState == BottomSheetBehavior.STATE_HIDDEN
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun showMessageListIsEmpty() {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(R.string.nothing_to_share)
            .setNeutralButton("OK", null)
            .show()
    }

    private fun toggleExpandingDescription() {
        binding.tdescription.apply {
            if (descriptionIsExpanded) {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            } else {
                maxLines = Int.MAX_VALUE
                ellipsize = null
            }
        }
        descriptionIsExpanded = !descriptionIsExpanded
    }

    private fun deletePlaylist() {
        moreMenuBSBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.do_you_want_to_delete_playlist))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }


    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylist(playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }


}