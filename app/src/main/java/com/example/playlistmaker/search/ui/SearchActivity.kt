package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.player.ui.Player
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var searchSavedString: String = SEARCH_DEF

    private lateinit var updateButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var rvSearchTrack: RecyclerView
    private lateinit var inputEditText: TextView

    private val trackList = ArrayList<Track>()
    private val searchAdapter = SearchAdapter(trackList)

    private lateinit var lastSearch: TextView
    private lateinit var rvHistoryList: RecyclerView
    private lateinit var clearHistoryBttn: Button
    private lateinit var historyLayout: LinearLayout

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var historyAdapter : SearchAdapter

    private lateinit var progressBar : ProgressBar

    private val historyTracks = ArrayList<Track>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val toolbarSets = findViewById<Toolbar>(R.id.my_toolbar)
        inputEditText = findViewById<EditText>(R.id.search_str)
        val clearButton = findViewById<ImageView>(R.id.clear_btn)

        rvSearchTrack = findViewById(R.id.trackList)
        placeholderMessage = findViewById(R.id.search_error)
        updateButton = findViewById(R.id.update_button)

        progressBar = findViewById(R.id.progressBar)

        rvSearchTrack.adapter = searchAdapter

        lastSearch = findViewById(R.id.last_search)
        rvHistoryList = findViewById(R.id.historyList)
        clearHistoryBttn = findViewById(R.id.clear_history_button)
        historyLayout = findViewById(R.id.history_layout)

        historyAdapter = SearchAdapter(historyTracks)
        rvHistoryList.adapter = historyAdapter

        val playerIntent = Intent(this, Player::class.java)

        searchAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {
                viewModel.toHistory(track)
                historyLayout.isVisible = false
                rvSearchTrack.isVisible = true
                if (clickDebounce()) {
                    startActivity(playerIntent)
                }
            }
        })

        historyAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {
                viewModel.toHistory(track)
                historyAdapter.notifyDataSetChanged()

                if (clickDebounce()) {
                    startActivity(playerIntent)
                }

            }
        })


        clearHistoryBttn.setOnClickListener(){
            viewModel.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyLayout.isVisible = false
            trackList.clear()
            rvSearchTrack.isVisible = true

        }

        toolbarSets.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            placeholderMessage.isVisible = false
            rvSearchTrack.isVisible = false
            updateButton.isVisible = false
        }

        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchSavedString = s.toString()

            }
        }
        inputEditText.addTextChangedListener(myTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
            }
            false
        }

        updateButton.setOnClickListener {
            showMessage(0)
            searchRequest()
        }
        
        inputEditText.setOnFocusChangeListener { v, hasFocus ->
            showHistory (hasFocus && inputEditText.text.isEmpty() )
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 showHistory  (inputEditText.hasFocus() && p0?.isEmpty() == true)

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewModel.searchStateLiveData.observe(this) { searchState ->
            when (searchState) {
                is SearchState.Success -> {
                    progressBar.isVisible = false
                    trackList.clear()
                    showMessage(0)
                    trackList.addAll(searchState.tracks)
                    searchAdapter.notifyDataSetChanged()

                }
                is SearchState.Nothing -> {
                    progressBar.isVisible = false
                    trackList.clear()
                    showMessage(1)

                }
                is SearchState.Error -> {
                    progressBar.isVisible = false
                    Log.d("TAG", "onFailure: ${searchState.t}")
                    showMessage(2)

                }
                is SearchState.History -> {
                    historyTracks.clear()
                    historyTracks.addAll(searchState.tracks)
                }
            }
        }


    }

    private fun showHistory(show : Boolean){
         if ((show) && (historyTracks.isNotEmpty())) {
            historyLayout.isVisible = true
            rvSearchTrack.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }
        else {
            historyLayout.isVisible = false
            rvSearchTrack.isVisible = true
        }
    }

    private fun searchRequest() {
        if (inputEditText.text.isNotEmpty()) {
            progressBar.isVisible = true

            viewModel.search(inputEditText.text.toString())

        }
    }


    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private val searchRunnable = Runnable { searchRequest() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showMessage(code: Int) {

            if (code == 0) {
                placeholderMessage.isVisible = false
                rvSearchTrack.isVisible = true
                updateButton.isVisible = false
            }
            else {
                placeholderMessage.isVisible = true
                rvSearchTrack.isVisible = false
                trackList.clear()
                searchAdapter.notifyDataSetChanged()
                when (code) {
                    1 -> {
                        placeholderMessage.text = getString(R.string.nothing_found)
                        placeholderMessage.setTextAppearance(R.style.MyErrorStyle1)
                        placeholderMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.nothing_found,0,0)
                        updateButton.isVisible = false
                    }
                    2 -> {
                        placeholderMessage.text = getString(R.string.something_went_wrong)
                        placeholderMessage.setTextAppearance(R.style.MyErrorStyle2)
                        placeholderMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.no_connection,0,0)
                        updateButton.isVisible = true
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchSavedString = savedInstanceState.getString(SEARCH_STRING, SEARCH_DEF)
        findViewById<EditText>(R.id.search_str).setText(searchSavedString)
    }

}