package com.example.playlistmaker.ui.search.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.viewmodel.SearchState
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingFound: View
    private lateinit var placeholderError: View
    private lateinit var refreshButton: Button
    private lateinit var searchHistoryScroll: View

    private lateinit var viewModel: SearchViewModel

    private var searchQuery: String = ""

    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private val historyAdapter = TrackAdapter(ArrayList())

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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


        viewModel = ViewModelProvider(
            this,
            Creator.provideSearchViewModelFactory(this)
        ).get(SearchViewModel::class.java)


        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> {
                    progressBar.visibility = View.GONE
                    adapter.updateTracks(state.tracks)
                    showPlaceholder(error = false, nothingFound = false)
                }
                is SearchState.Empty -> {
                    progressBar.visibility = View.GONE
                    showPlaceholder(error = false, nothingFound = true)
                }
                is SearchState.Error -> {
                    progressBar.visibility = View.GONE
                    showPlaceholder(error = true, nothingFound = false)
                }
                is SearchState.History -> {
                    searchHistoryScroll.visibility = if (state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                    if (state.tracks.isNotEmpty()) {
                        (findViewById<RecyclerView>(R.id.history_recycler)).apply {
                            layoutManager = LinearLayoutManager(this@SearchActivity)
                            adapter = historyAdapter
                        }
                        historyAdapter.updateTracks(state.tracks)
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }

        adapter.setOnItemClickListener { track ->
            viewModel.addToHistory(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra("track", track))
        }
        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
        historyAdapter.setOnItemClickListener { track ->
            viewModel.addToHistory(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java).putExtra("track", track))
        }
        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        refreshButton.setOnClickListener {
            viewModel.forceSearch(searchEditText.text.toString())
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
                viewModel.onTextChanged(searchQuery)
                if (searchQuery.isBlank() && searchEditText.hasFocus()) viewModel.loadHistory()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrEmpty()) viewModel.loadHistory()
            else searchHistoryScroll.visibility = View.GONE
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            trackList.clear()
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            showPlaceholder(error = false, nothingFound = false)
            if (searchEditText.hasFocus()) viewModel.loadHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderError.visibility = View.GONE
        searchHistoryScroll.visibility = View.GONE
    }

    private fun showPlaceholder(error: Boolean, nothingFound: Boolean) {
        progressBar.visibility = View.GONE
        placeholderError.visibility = if (error) View.VISIBLE else View.GONE
        placeholderNothingFound.visibility = if (nothingFound) View.VISIBLE else View.GONE
        recyclerView.visibility = if (!error && !nothingFound) View.VISIBLE else View.GONE
        searchHistoryScroll.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        currentFocus?.windowToken?.let { imm?.hideSoftInputFromWindow(it, 0) }
    }
}
//new 152