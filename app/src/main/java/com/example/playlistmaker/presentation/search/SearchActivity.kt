package com.example.playlistmaker.presentation.search


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingFound: View
    private lateinit var placeholderError: View
    private lateinit var refreshButton: Button
    private lateinit var searchHistoryScroll: View

    private lateinit var handler: Handler
    private lateinit var interactor: TracksInteractor
    private lateinit var historyInteractor: SearchHistoryInteractor

    private var searchQuery: String = ""

    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private val historyAdapter = TrackAdapter(ArrayList())

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        interactor = Creator.provideTracksInteractor()
        historyInteractor = Creator.provideSearchHistoryInteractor(this)

        handler = Handler(mainLooper)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { finish() }

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
            historyInteractor.addTrack(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra("track", track))
        }

        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        historyAdapter.setOnItemClickListener { track ->
            historyInteractor.addTrack(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra("track", track))
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
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
            if (hasFocus && searchEditText.text.isNullOrEmpty()) showSearchHistory() else hideSearchHistory()
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            trackList.clear()
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            showPlaceholder(error = false, nothingFound = false)
            if (searchEditText.hasFocus() && searchEditText.text.isNullOrEmpty()) showSearchHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun search(query: String) {
        showLoading()
        interactor.searchTracks(query, object : TracksInteractor.Consumer {
            override fun consume(tracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    adapter.updateTracks(tracks)
                    if (tracks.isEmpty()) {
                        showPlaceholder(error = false, nothingFound = true)
                    } else {
                        showPlaceholder(error = false, nothingFound = false)
                    }
                }
            }
        })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun showPlaceholder(error: Boolean, nothingFound: Boolean) {
        progressBar.visibility = View.GONE
        placeholderError.visibility = if (error) View.VISIBLE else View.GONE
        placeholderNothingFound.visibility = if (nothingFound) View.VISIBLE else View.GONE
        recyclerView.visibility = if (!error && !nothingFound) View.VISIBLE else View.GONE
    }

    private fun showSearchHistory() {
        val history = historyInteractor.getHistory()
        if (history.isNotEmpty()) {
            searchHistoryScroll.visibility = View.VISIBLE
            historyAdapter.updateTracks(history)
            recyclerView.visibility = View.GONE
        }
    }

    private fun hideSearchHistory() {
        searchHistoryScroll.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.windowToken?.let { imm?.hideSoftInputFromWindow(it, 0) }
    }
}
//new 152