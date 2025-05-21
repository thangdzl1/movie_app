package com.example.television_movies

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class Description : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val selectedMovie = item as Movie

        // Gán tiêu đề phim
        viewHolder.title.text = selectedMovie.getDisplayTitle()

        // Gán phụ đề hiển thị điểm đánh giá, nếu không có thì hiển thị "N/A"
        val rating = selectedMovie.vote_average ?: "N/A"
        viewHolder.subtitle.text = "Rating: $rating"

        // Gán mô tả nội dung nếu có, nếu không thì hiện thông báo mặc định
        viewHolder.body.text = selectedMovie.overview ?: "No description provided."
    }
}
