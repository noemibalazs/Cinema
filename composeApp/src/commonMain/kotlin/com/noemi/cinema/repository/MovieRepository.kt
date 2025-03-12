package com.noemi.cinema.repository

import com.noemi.cinema.database.MovieEntity
import com.noemi.cinema.model.Movies
import com.noemi.cinema.model.Review
import com.noemi.cinema.model.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun loadTrailers(movieId: Int): Flow<List<Trailer>>

    fun loadReviews(movieId: Int): Flow<List<Review>>

    suspend fun loadTopRatedMovies(page: Int): Movies

    suspend fun loadPopularMovies(page: Int): Movies

    fun observeMovies(): Flow<List<MovieEntity>>

    suspend fun getMovie(movieId: Int): MovieEntity

    suspend fun insertMovie(movie: MovieEntity)
}