package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
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
                        updateButton.visibility = View.GONE
                    }
                    2 -> {
                        placeholderMessage.text = getString(R.string.something_went_wrong)
                        placeholderMessage.setTextAppearance(R.style.MyErrorStyle2)
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