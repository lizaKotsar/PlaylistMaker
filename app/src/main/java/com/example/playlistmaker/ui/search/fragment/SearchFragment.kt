package com.example.playlistmaker.ui.search.fragment



import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.viewmodel.SearchState
import com.example.playlistmaker.ui.search.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
        fun newInstance() = SearchFragment()
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private var searchQuery: String = ""
    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private val historyAdapter = TrackAdapter(ArrayList())

    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tracksRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksRecycler.adapter = adapter

        binding.historyRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.historyRecycler.adapter = historyAdapter

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateTracks(state.tracks)
                    showPlaceholder(error = false, nothingFound = false)
                }
                is SearchState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    showPlaceholder(error = false, nothingFound = true)
                }
                is SearchState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showPlaceholder(error = true, nothingFound = false)
                }
                is SearchState.History -> {
                    binding.searchHistoryScroll.visibility =
                        if (state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                    if (state.tracks.isNotEmpty()) {
                        historyAdapter.updateTracks(state.tracks)
                        binding.tracksRecycler.visibility = View.GONE
                    }
                }
            }
        }


        adapter.setOnItemClickListener { track ->
            viewModel.addToHistory(track)
            val args = bundleOf("track" to track)
            findNavController().navigate(R.id.action_search_to_player, args)
        }
        historyAdapter.setOnItemClickListener { track ->
            viewModel.addToHistory(track)
            val args = bundleOf("track" to track)
            findNavController().navigate(R.id.action_search_to_player, args)
        }


        binding.clearHistoryButton.setOnClickListener { viewModel.clearHistory() }
        binding.refreshButton.setOnClickListener {
            viewModel.forceSearch(binding.searchEditText.text.toString())
        }
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.text?.clear()
            hideKeyboard()
            trackList.clear()
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            showPlaceholder(error = false, nothingFound = false)
            if (binding.searchEditText.hasFocus()) viewModel.loadHistory()
        }

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            binding.searchEditText.setText(searchQuery)
            binding.searchEditText.setSelection(searchQuery.length)
            binding.clearIcon.visibility = if (searchQuery.isEmpty()) View.GONE else View.VISIBLE
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.onTextChanged(searchQuery)
                if (searchQuery.isBlank() && binding.searchEditText.hasFocus()) viewModel.loadHistory()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        }
        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isNullOrEmpty()) {
                viewModel.loadHistory()
            } else {
                binding.searchHistoryScroll.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(textWatcher)
        textWatcher = null
        _binding = null
        super.onDestroyView()
    }

    private fun showLoading() = with(binding) {
        progressBar.visibility = View.VISIBLE
        tracksRecycler.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderError.visibility = View.GONE
        searchHistoryScroll.visibility = View.GONE
    }

    private fun showPlaceholder(error: Boolean, nothingFound: Boolean) = with(binding) {
        progressBar.visibility = View.GONE
        placeholderError.visibility = if (error) View.VISIBLE else View.GONE
        placeholderNothingFound.visibility = if (nothingFound) View.VISIBLE else View.GONE
        tracksRecycler.visibility = if (!error && !nothingFound) View.VISIBLE else View.GONE
        searchHistoryScroll.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        view?.windowToken?.let { imm?.hideSoftInputFromWindow(it, 0) }
    }
}