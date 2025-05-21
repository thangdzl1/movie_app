//*
package com.example.television_movies.network

import com.example.television_movies.Movie
import com.example.television_movies.MovieResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object {
            const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "de31b870b958936ab2702e760566a400" // Thay bằng API Key cá nhân của bạn
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500" // Kích thước ảnh w500
    }

    // API để lấy danh sách phim đang phổ biến
    @GET("movie/popular")
    suspend fun fetchPopularMovies(
        @Query("api_key") key: String = API_KEY,
        @Query("language") lang: String = "en-US",
        @Query("page") pageIndex: Int = 1
    ): Response<MovieResponse> // Trả về danh sách phim (MovieResponse)

    // API để lấy danh sách TV show đang phổ biến
    @GET("tv/popular")
    suspend fun fetchPopularTvShows(
        @Query("api_key") key: String = API_KEY,
        @Query("language") lang: String = "en-US",
        @Query("page") pageIndex: Int = 1
    ): Response<MovieResponse> // Sử dụng MovieResponse do cấu trúc tương đồng

    // API để lấy thông tin chi tiết của một bộ phim cụ thể
    @GET("movie/{movie_id}")
    suspend fun fetchMovieDetails(
        @Path("movie_id") id: Int,
        @Query("api_key") key: String = API_KEY,
        @Query("language") lang: String = "en-US"
    ): Response<Movie> // Trả về đối tượng Movie

    // API để lấy thông tin chi tiết của một TV show cụ thể
    @GET("tv/{tv_id}")
    suspend fun fetchTvShowDetails(
        @Path("tv_id") id: Int,
        @Query("api_key") key: String = API_KEY,
        @Query("language") lang: String = "en-US"
    ): Response<Movie> // Cũng dùng Movie để ánh xạ dữ liệu trả về
}
