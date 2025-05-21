package com.example.television_movies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.television_movies.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

class MainFragment : BrowseSupportFragment() {

    private val TAG = "MainFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows()
        setupEventListeners()
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireActivity(), R.color.fastlane_background)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = Card_movies() // Sửa từ Card_movies thành CardPresenter

        lifecycleScope.launch {
            loadPopularMovies(rowsAdapter, cardPresenter)
            loadPopularTvShows(rowsAdapter, cardPresenter)
            adapter = rowsAdapter
        }
    }

    private suspend fun loadPopularMovies(rowsAdapter: ArrayObjectAdapter, cardPresenter: Presenter) {
        try {
            val response = RetrofitClient.instance.fetchPopularMovies()
            if (response.isSuccessful) {
                val movies = response.body()?.results
                movies?.takeIf { it.isNotEmpty() }?.let {
                    val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                    listRowAdapter.addAll(0, it)
                    val header = HeaderItem(0, "Popular Movies")
                    rowsAdapter.add(ListRow(header, listRowAdapter))
                }
            } else {
                Log.e(TAG, "Error fetching popular movies: ${response.code()}")
                Toast.makeText(context, "Error fetching movies: ${response.message()}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error fetching popular movies", e)
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching popular movies", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun loadPopularTvShows(rowsAdapter: ArrayObjectAdapter, cardPresenter: Presenter) {
        try {
            val tvResponse = RetrofitClient.instance.fetchPopularTvShows()
            if (tvResponse.isSuccessful) {
                val tvShows = tvResponse.body()?.results
                tvShows?.takeIf { it.isNotEmpty() }?.let {
                    val tvListRowAdapter = ArrayObjectAdapter(cardPresenter)
                    tvListRowAdapter.addAll(0, it)
                    val tvHeader = HeaderItem(1, "Popular TV Shows")
                    rowsAdapter.add(ListRow(tvHeader, tvListRowAdapter))
                }
            } else {
                Log.e(TAG, "Error fetching popular TV shows: ${tvResponse.code()}")
                Toast.makeText(context, "Error fetching TV shows: ${tvResponse.message()}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error fetching popular TV shows", e)
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching popular TV shows", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is Movie && context != null) {
                Log.d(TAG, "Item: ${item.getDisplayTitle()}")
                val intent = Intent(context, MoviesDetails::class.java) // Sửa thành DetailsActivity
                intent.putExtra(MoviesDetails.MOVIE_ITEM, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    MoviesDetails.SHARED_ELEMENT_NAME
                ).toBundle()
                startActivity(intent, bundle)
            }
        }
    }
}