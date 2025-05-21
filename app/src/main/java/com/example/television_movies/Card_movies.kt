package com.example.television_movies

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.television_movies.network.ApiService

private const val CARD_WIDTH_PX = 320
private const val CARD_HEIGHT_PX = 200

class Card_movies : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cardView = inflater.inflate(R.layout.custom_image_card_item, parent, false) as ImageCardView

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = movie.getDisplayTitle()
        cardView.contentText = "⭐ ${movie.vote_average ?: "N/A"}"
        cardView.setMainImageDimensions(CARD_WIDTH_PX, CARD_HEIGHT_PX)

        val imageUrl = when {
            !movie.poster_path.isNullOrEmpty() -> ApiService.IMAGE_BASE_URL + movie.poster_path
            !movie.backdrop_path.isNullOrEmpty() -> ApiService.IMAGE_BASE_URL + movie.backdrop_path
            else -> null
        }

        if (imageUrl != null) {
            Glide.with(viewHolder.view.context)
                .load(imageUrl)
                .centerCrop()
                .error(ContextCompat.getDrawable(cardView.context, R.drawable.movie_placeholder))
                .into(object : CustomTarget<Drawable>(CARD_WIDTH_PX, CARD_HEIGHT_PX) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        cardView.mainImageView.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        cardView.mainImageView.setImageDrawable(placeholder)
                    }
                })
        } else {
            cardView.mainImageView.setImageDrawable(
                ContextCompat.getDrawable(cardView.context, R.drawable.movie_placeholder)
            )
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}
