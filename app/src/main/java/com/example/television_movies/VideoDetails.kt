package com.example.television_movies

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.television_movies.network.ApiService

class VideoDetails : DetailsSupportFragment() {

    private val TAG = "VideoDetailsFragment"

    private var selectedMovie: Movie? = null
    private lateinit var backgroundController: DetailsSupportFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var adapterObject: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment created")

        backgroundController = DetailsSupportFragmentBackgroundController(this)
        selectedMovie = activity?.intent?.getSerializableExtra(MoviesDetails.MOVIE_ITEM) as? Movie

        if (selectedMovie == null) {
            Log.e(TAG, "No movie found!")
            activity?.finish()
            return
        }

        setupUI()
        loadBackdropImage(selectedMovie!!)
    }

    private fun setupUI() {
        presenterSelector = ClassPresenterSelector()
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(Description()).apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.detail_background))
            setActionsBackgroundColor(ContextCompat.getColor(requireContext(), R.color.detail_actions_background))
            onActionClickedListener = OnActionClickedListener { action ->
                when (action.id) {
                    1L -> Log.d(TAG, "Watch Trailer clicked")
                    2L -> Log.d(TAG, "Add to Favorites clicked")
                }
            }
        }

        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        adapterObject = ArrayObjectAdapter(presenterSelector)
        adapter = adapterObject

        setupDetailsOverviewRow()
    }

    private fun setupDetailsOverviewRow() {
        val row = DetailsOverviewRow(selectedMovie)

        // Load poster image
        val posterUrl = selectedMovie?.poster_path?.let { ApiService.IMAGE_BASE_URL + it }
        if (posterUrl != null) {
            Glide.with(this)
                .asBitmap()
                .load(posterUrl)
                .error(R.drawable.movie_placeholder)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        row.setImageBitmap(requireContext(), resource)
                        adapterObject.notifyArrayItemRangeChanged(0, adapterObject.size())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            row.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.movie_placeholder))
        }

        // Add action buttons
        row.actionsAdapter = ArrayObjectAdapter().apply {
            add(Action(1, "Watch Trailer"))
            add(Action(2, "Add to Favorites"))
        }

        adapterObject.add(row)
    }

    private fun loadBackdropImage(movie: Movie) {
        val backdropUrl = when {
            !movie.backdrop_path.isNullOrEmpty() -> ApiService.IMAGE_BASE_URL + movie.backdrop_path
            !movie.poster_path.isNullOrEmpty() -> ApiService.IMAGE_BASE_URL + movie.poster_path
            else -> null
        }

        if (backdropUrl != null) {
            backgroundController.enableParallax()
            Glide.with(this)
                .asBitmap()
                .load(backdropUrl)
                .centerCrop()
                .error(R.drawable.default_background)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        backgroundController.coverBitmap = resource
                        adapterObject.notifyArrayItemRangeChanged(0, adapterObject.size())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            backgroundController.setSolidColor(
                ContextCompat.getColor(requireContext(), R.color.default_detail_background_color)
            )
        }
    }
}
