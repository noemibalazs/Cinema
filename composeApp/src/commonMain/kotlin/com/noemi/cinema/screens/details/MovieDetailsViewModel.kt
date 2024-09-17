package com.noemi.cinema.screens.details

import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.base.BaseViewModel
import com.noemi.cinema.model.Movie
import com.noemi.cinema.model.Review
import com.noemi.cinema.model.Trailer
import com.noemi.cinema.repository.MovieRepository
import com.noemi.cinema.utils.toMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    konnectivity: Konnectivity,
    private val repository: MovieRepository
) : BaseViewModel<Movie>(konnectivity, repository) {

    private var _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private var _trailers = MutableStateFlow(emptyList<Trailer>())
    val trailersState: StateFlow<List<Trailer>> = _trailers.asStateFlow()

    private var _reviews = MutableStateFlow(emptyList<Review>())
    val reviewsState: StateFlow<List<Review>> = _reviews.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    private var _movie = MutableStateFlow(Movie())
    override val payloadState: StateFlow<Movie> = _movie.asStateFlow()

    fun loadMovieDetails(movieId: Int) {

        viewModelScope.launch {

            if (movieId == 0) _errorState.emit("Invalid movie id, try it again!")
            else {
                _loadingState.emit(true)
                _movie.emit(repository.getMovie(movieId).toMovie())
                loadTrailersAndReviews(movieId)
            }
        }
    }

    private fun loadTrailersAndReviews(movieId: Int) {
        viewModelScope.launch {
            val trailersSource = repository.loadTrailers(movieId)
            val reviewsSource = repository.loadReviews(movieId)

            combine(trailersSource, reviewsSource) { trailers, reviews ->
                trailers to reviews
            }
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading trailers and reviews.")
                }
                .collectLatest {
                    _trailers.emit(it.first)
                    _reviews.emit(it.second)

                    _loadingState.emit(false)
                }
        }
    }

    fun getMovieRating(): String {
        when (_movie.value.rating.toString().length >= 4) {
            true -> {
                val rating = _movie.value.rating.toString().substring(0, 4)
                return "Rating: $rating"
            }

            else -> {
                val rating = _movie.value.rating.toString()
                return "Rating: $rating"
            }
        }
    }
}