package com.noemi.cinema.repository

import com.noemi.cinema.database.MovieDAO
import com.noemi.cinema.database.MovieEntity
import com.noemi.cinema.model.Review
import com.noemi.cinema.model.Trailer
import com.noemi.cinema.service.MovieService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepositoryImpl(
    private val service: MovieService,
    private val movieDAO: MovieDAO,
    private val dispatcher: CoroutineDispatcher
) : MovieRepository {

    override fun loadTrailers(movieId: Int): Flow<List<Trailer>> = flow {
        emit(service.loadTrailers(movieId).trailers)
    }.flowOn(dispatcher)

    override fun loadReviews(movieId: Int): Flow<List<Review>> = flow {
        emit(service.loadReviews(movieId).reviews)
    }.flowOn(dispatcher)

    override fun observeMovies(): Flow<List<MovieEntity>> = movieDAO.observeMovies()

    override suspend fun getMovie(movieId: Int): MovieEntity = movieDAO.getMovie(movieId)

    override suspend fun insertMovie(movie: MovieEntity) = movieDAO.insertMovie(movie)
}