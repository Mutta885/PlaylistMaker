package com.example.playlistmaker.library.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.player.ui.Player
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchAdapter
import com.example.playlistmaker.search.ui.SearchFragment
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val favoritesTracks = ArrayList<Track>()
    private lateinit var favoritesAdapter : SearchAdapter

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        favoritesAdapter = SearchAdapter(favoritesTracks)
        binding.favoriteList.adapter = favoritesAdapter

        viewModel.favoriteStateLiveData.observe(viewLifecycleOwner) {favoriteState ->
            when (favoriteState) {
                is FavoritesState.Empty -> showEmpty()
                is FavoritesState.Loading -> showLoading()
                is FavoritesState.Content -> showContent(favoriteState)

            }
        }

        val playerIntent = Intent(requireContext(), Player::class.java)

        favoritesAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {

                if (clickDebounce()) {
                    playerIntent.putExtra("TRACK", Gson().toJson(track))
                    startActivity(playerIntent)
                }
            }
        })



    }


    private fun showEmpty() {
        binding.favoriteList.isVisible = false
        binding.progressBar.isVisible = false
        binding.placeholderText.isVisible = true
        binding.placeholderImage.isVisible = true
    }

    private fun showLoading() {
        binding.favoriteList.isVisible = false
        binding.progressBar.isVisible = true
        binding.placeholderText.isVisible = false
        binding.placeholderImage.isVisible = false
    }

      private fun showContent(favoriteState: FavoritesState.Content) {

       favoritesTracks.clear()

       favoritesTracks.addAll(favoriteState.favorites)
        binding.favoriteList.adapter?.notifyDataSetChanged()



        binding.favoriteList.isVisible = true
        binding.progressBar.isVisible = false
        binding.placeholderText.isVisible = false
        binding.placeholderImage.isVisible = false
    }

    private var isClickAllowed = true
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false

            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }

        }
        return current
    }

    override fun onResume() {
        viewModel.getFavorites()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}