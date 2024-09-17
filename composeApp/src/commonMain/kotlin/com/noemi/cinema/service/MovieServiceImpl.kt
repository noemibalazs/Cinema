package com.noemi.cinema.service

import com.noemi.cinema.BuildKonfig.API_KEY
import com.noemi.cinema.model.Movies
import com.noemi.cinema.model.Reviews
import com.noemi.cinema.model.Trailers
import com.noemi.cinema.utils.Constants.KEY_PARAMETER
import com.noemi.cinema.utils.Constants.KEY_PAGE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieServiceImpl(private val client: HttpClient) : MovieService {

    override suspend fun loadTopRatedMovies(page: Int): Movies {
        val movies = client.get("movie/top_rated") {
            parameter(KEY_PARAMETER, API_KEY)
            parameter(KEY_PAGE, page)
        }
        return movies.body()
    }

    override suspend fun loadPopularMovies(page: Int): Movies {
        val movies = client.get("movie/popular") {
            parameter(KEY_PARAMETER, API_KEY)
            parameter(KEY_PAGE, page)
        }
        return movies.body()
    }

    override suspend fun loadTrailers(movieId: Int): Trailers {
        val trailers = client.get("movie/$movieId/videos") {
            parameter(KEY_PARAMETER, API_KEY)
        }
        return trailers.body()
    }

    override suspend fun loadReviews(movieId: Int): Reviews {
        val reviews = client.get("movie/$movieId/reviews") {
            parameter(KEY_PARAMETER, API_KEY)
        }
        return reviews.body()
    }
}