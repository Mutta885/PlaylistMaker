package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.SearchAdapter.OnClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchSavedString: String = SEARCH_DEF

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

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

    private lateinit var searchHistory : SearchHistory

    private lateinit var historyAdapter : SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarSets = findViewById<Toolbar>(R.id.my_toolbar)
        inputEditText = findViewById<EditText>(R.id.search_str)
        val clearButton = findViewById<ImageView>(R.id.clear_btn)

        rvSearchTrack = findViewById(R.id.trackList)
        placeholderMessage = findViewById(R.id.search_error)
        updateButton = findViewById(R.id.update_button)

        rvSearchTrack.adapter = searchAdapter

        lastSearch = findViewById(R.id.last_search)
        rvHistoryList = findViewById(R.id.historyList)
        clearHistoryBttn = findViewById(R.id.clear_history_button)
        historyLayout = findViewById(R.id.history_layout)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        historyAdapter = SearchAdapter(searchHistory.historyList)
        rvHistoryList.adapter = historyAdapter

        val playerIntent = Intent(this, Player::class.java)

        searchAdapter.setOnClickListener(object : SearchAdapter.OnClickListener {
            override fun onClick(track: Track) {
                searchHistory.addTrackToHistory(track)
                historyLayout.visibility = View.GONE
                rvSearchTrack.visibility = View.VISIBLE

                startActivity(playerIntent)
            }
        })

        historyAdapter.setOnClickListener(object : SearchAdapter.OnClickListener{
            override fun onClick(track: Track) {
                searchHistory.addTrackToHistory(track)
                historyAdapter.notifyDataSetChanged()

                startActivity(playerIntent)

            }
        })


        clearHistoryBttn.setOnClickListener(){
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
            rvSearchTrack.visibility = View.VISIBLE

        }

        toolbarSets.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
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

    }

    private fun showHistory(show : Boolean){
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)
        if ((show) && (searchHistory.historyList.isNotEmpty())) {
            historyLayout.visibility = View.VISIBLE
            rvSearchTrack.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }
        else {
            historyLayout.visibility = View.GONE
            rvSearchTrack.visibility = View.VISIBLE
        }
    }

    private fun searchRequest() {
        if (inputEditText.text.isNotEmpty()) {
            iTunesService.search(inputEditText.text.toString()).enqueue(object :
                Callback<SongsResponse> {
                override fun onResponse(call: Call<SongsResponse>,
                                        response: Response<SongsResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            searchAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showMessage(1)
                        } else {
                            showMessage(0)
                        }
                    } else {
                        showMessage(2)
                    }
                }

                override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                    showMessage(2)
                }

            })
        }

    }

    private fun showMessage(code: Int) {

            if (code == 0) {
                placeholderMessage.visibility = View.GONE
                rvSearchTrack.visibility = View.VISIBLE
                updateButton.visibility = View.GONE
            }
            else {

                placeholderMessage.visibility = View.VISIBLE
                rvSearchTrack.visibility = View.GONE
                trackList.clear()
                searchAdapter.notifyDataSetChanged()
                when (code) {
                    1 -> {
                        placeholderMessage.text = getString(R.string.nothing_found)
                        placeholderMessage.setTextAppearance(R.style.MyErrorStyle1)
                        placeholderMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.nothing_found,0,0)
                        updateButton.visibility = View.GONE
                    }
                    2 -> {
                        placeholderMessage.text = getString(R.string.something_went_wrong)
                        placeholderMessage.setTextAppearance(R.style.MyErrorStyle2)
                        placeholderMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.no_connection,0,0)
                        updateButton.visibility = View.VISIBLE
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchSavedString = savedInstanceState.getString(SEARCH_STRING, SEARCH_DEF)
        findViewById<EditText>(R.id.search_str).setText(searchSavedString)
    }

}