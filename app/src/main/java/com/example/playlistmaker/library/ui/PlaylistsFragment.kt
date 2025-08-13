package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private var adapter: PlaylistAdapter? = null

    private val viewModel by viewModel<PlaylistsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_newPlaylistFragment
            )
        }

        adapter = PlaylistAdapter(
            viewType = PlaylistAdapter.FROM_PLAYLIST,
            onItemClick = { playlist -> })  //23 sprint
        binding.recyclerView.adapter = adapter
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }



    private fun showPlaceholder() {
        binding.apply {
            recyclerView.isVisible = false
            //progressbar.isVisible = false
            placeholderImage.isVisible = true
            placeholderText.isVisible = true
            //llPlaceholder.isVisible = true
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            recyclerView.isVisible = true
            //progressbar.isVisible = false
            //llPlaceholder.isVisible = false
            placeholderImage.isVisible = false
            placeholderText.isVisible = false
        }
        adapter?.clearList()
        adapter?.submitList(playlists as ArrayList)
        adapter?.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.recyclerView.adapter = null
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}