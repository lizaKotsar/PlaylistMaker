package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.model.Track
import com.example.playlistmaker.data.model.TrackAdapter
import com.example.playlistmaker.data.model.TrackResponse
import com.example.playlistmaker.data.model.network.ITunesApi
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

import com.example.playlistmaker.data.model.SearchHistory
import android.util.Log
class SearchActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingFound: View
    private lateinit var placeholderError: View
    private lateinit var refreshButton: Button
    private lateinit var searchHistory: SearchHistory
    private var searchQuery: String = ""
    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private lateinit var searchHistoryScroll: View
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val historyAdapter = TrackAdapter(ArrayList())
    private lateinit var handler: Handler


    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchRunnable = Runnable {
        if (searchQuery.isNotEmpty()) {
            hideSearchHistory()
            search(searchQuery)
        }
    }
    private fun search(query: String) {

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderError.visibility = View.GONE

        iTunesService.search(query)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        val tracks = response.body()?.results.orEmpty()
                        for (t in tracks) {
                            Log.d("API_TRACK", "Name=${t.trackName}, previewUrl=${t.previewUrl}")
                        }
                        adapter.updateTracks(tracks)
                        if (tracks.isEmpty()) {
                            showPlaceholder(error = false, nothingFound = true)
                        } else {
                            showPlaceholder(error = false, nothingFound = false)
                        }
                    } else {
                        showPlaceholder(error = true, nothingFound = false)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showPlaceholder(error = true, nothingFound = false)
                }
            })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun showPlaceholder(error: Boolean, nothingFound: Boolean) {
        progressBar.visibility = View.GONE
        placeholderError.visibility = if (error) View.VISIBLE else View.GONE
        placeholderNothingFound.visibility = if (nothingFound) View.VISIBLE else View.GONE
        recyclerView.visibility = if (!error && !nothingFound) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

         handler = Handler(mainLooper)

        searchHistory = SearchHistory(getSharedPreferences("playlist_preferences", MODE_PRIVATE))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        progressBar = findViewById(R.id.progressBar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearIcon = findViewById(R.id.clear_icon)
        recyclerView = findViewById(R.id.tracksRecycler)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderError = findViewById(R.id.placeholder_error)
        refreshButton = findViewById(R.id.refresh_button)
        searchHistoryScroll = findViewById(R.id.search_history_scroll)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { track ->
            Log.d("SearchActivity", "track.previewUrl = ${track.previewUrl}")
            searchHistory.addTrack(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }

        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)

        historyRecycler.layoutManager = LinearLayoutManager(this)

        historyRecycler.adapter = historyAdapter

        historyAdapter.setOnItemClickListener { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            hideSearchHistory()

        }



        refreshButton.setOnClickListener {
            if (searchQuery.isNotEmpty()) {
                search(searchQuery)
            }
        }

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            searchEditText.setText(searchQuery)
            searchEditText.setSelection(searchQuery.length)
            clearIcon.visibility = if (searchQuery.isEmpty()) View.GONE else View.VISIBLE
        }



        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            trackList.clear()
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            showPlaceholder(error = false, nothingFound = false)

            if (searchEditText.hasFocus() && searchEditText.text.isNullOrEmpty()) {
                showSearchHistory()
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.windowToken?.let {
            inputMethodManager?.hideSoftInputFromWindow(it, 0)
        }
    }

    private fun showSearchHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {


            searchHistoryScroll.visibility = View.VISIBLE
            historyAdapter.updateTracks(history)
            recyclerView.visibility = View.GONE

            Log.d("SearchActivity", "showSearchHistory: история = ${history.map { it.trackName }}")
        }
    }

    private fun hideSearchHistory() {
        searchHistoryScroll.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }
}
