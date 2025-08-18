package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.Player
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var searchSavedString: String = SearchFragment.SEARCH_DEF
    private val trackList = ArrayList<Track>()
    private val searchAdapter = SearchAdapter(trackList)
    private val historyTracks = ArrayList<Track>()
    private lateinit var historyAdapter : SearchAdapter

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private var isClickAllowed = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true
        binding.trackList.adapter = searchAdapter

        historyAdapter = SearchAdapter(historyTracks)
        binding.historyList.adapter = historyAdapter


        searchAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {
                viewModel.toHistory(track)
                binding.historyLayout.isVisible = false
                binding.trackList.isVisible = true
                if (clickDebounce()) {
                    findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToPlayer(Gson().toJson(track)))
                }
            }
        })

        historyAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {
                viewModel.toHistory(track)
                historyAdapter.notifyDataSetChanged()

                if (clickDebounce()) {
                    findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToPlayer(Gson().toJson(track)))
                }

            }
        })


        binding.clearHistoryButton.setOnClickListener(){
            viewModel.clearHistory()
            historyAdapter.notifyDataSetChanged()
            binding.historyLayout.isVisible = false
            trackList.clear()
            binding.trackList.isVisible = true

        }


        binding.clearBtn.setOnClickListener {
            binding.searchStr.setText("")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchStr.windowToken, 0)
            binding.searchError.isVisible = false
            binding.trackList.isVisible = false
            binding.updateButton.isVisible = false
        }

        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearBtn.visibility = clearButtonVisibility(s)
                searchDebounce(s.toString())
                //latestSearchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                searchSavedString = s.toString()

            }
        }
        binding.searchStr.addTextChangedListener(myTextWatcher)

        binding.searchStr.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
            }
            false
        }

        binding.updateButton.setOnClickListener {
            showMessage(0)
            searchRequest()
        }

        binding.searchStr.setOnFocusChangeListener { v, hasFocus ->
            showHistory (hasFocus && binding.searchStr.text.isEmpty() )
        }

        binding.searchStr.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                showHistory  (binding.searchStr.hasFocus() && p0?.isEmpty() == true)

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewModel.searchStateLiveData.observe(viewLifecycleOwner) { searchState ->
            when (searchState) {
                is SearchState.Success -> {
                    binding.progressBar.isVisible = false
                    trackList.clear()
                    showMessage(0)
                    trackList.addAll(searchState.tracks)
                    searchAdapter.notifyDataSetChanged()

                }
                is SearchState.Nothing -> {
                    binding.progressBar.isVisible = false
                    trackList.clear()
                    showMessage(1)

                }
                is SearchState.Error -> {
                    binding.progressBar.isVisible = false
                    Log.d("TAG", "onFailure: ${searchState.error}")
                    showMessage(2)

                }
                is SearchState.History -> {
                    historyTracks.clear()
                    historyTracks.addAll(searchState.tracks)
                }
                else -> {}
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showHistory(show : Boolean){
        if ((show) && (historyTracks.isNotEmpty())) {
            binding.historyLayout.isVisible = true
            binding.trackList.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }
        else {
            binding.historyLayout.isVisible = false
            binding.trackList.isVisible = true
        }
    }

    private fun searchRequest() {
        if (binding.searchStr.text.isNotEmpty()) {
            binding.progressBar.isVisible = true

            viewModel.search(binding.searchStr.text.toString())

        }
    }




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

    fun searchDebounce(changedText: String) {

        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText

        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest()//(changedText)
        }


    }

    private fun showMessage(code: Int) {

        if (code == 0) {
            binding.searchError.isVisible = false
            binding.trackList.isVisible = true
            binding.updateButton.isVisible = false
        }
        else {
            binding.searchError.isVisible = true
            binding.trackList.isVisible = false
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            when (code) {
                1 -> {
                    binding.searchError.text = getString(R.string.nothing_found)
                    binding.searchError.setTextAppearance(R.style.MyErrorStyle1)
                    binding.searchError.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                        R.drawable.nothing_found,0,0)
                    binding.updateButton.isVisible = false
                }
                2 -> {
                    binding.searchError.text = getString(R.string.something_went_wrong)
                    binding.searchError.setTextAppearance(R.style.MyErrorStyle2)
                    binding.searchError.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                        R.drawable.no_connection,0,0)
                    binding.updateButton.isVisible = true
                }
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_STRING, searchSavedString)
        super.onSaveInstanceState(outState)

    }

    companion object {
        private const val SEARCH_STRING = "SEARCH_STRING"
        val SEARCH_DEF = ""

        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }



}