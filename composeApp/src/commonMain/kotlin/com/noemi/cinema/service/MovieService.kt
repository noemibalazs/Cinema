package com.noemi.cinema.service

import com.noemi.cinema.model.Movies
import com.noemi.cinema.model.Reviews
import com.noemi.cinema.model.Trailers

interface MovieService {

    suspend fun loadTopRatedMovies(page: Int): Movies

    suspend fun loadPopularMovies(page: Int): Movies

    suspend fun loadTrailers(movieId: Int): Trailers

    suspend fun loadReviews(movieId: Int): Reviews
}
