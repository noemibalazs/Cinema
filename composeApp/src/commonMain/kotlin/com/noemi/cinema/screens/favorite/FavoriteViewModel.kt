package com.noemi.cinema.screens.favorite

import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.base.BaseViewModel
import com.noemi.cinema.model.Movie
import com.noemi.cinema.repository.MovieRepository
import com.noemi.cinema.utils.toMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(
    konnectivity: Konnectivity,
    private val repository: MovieRepository
) : BaseViewModel<List<Movie>>(konnectivity, repository) {

    private val favoriteMovies = MutableStateFlow(emptyList<Movie>())
    override val payloadState: StateFlow<List<Movie>> = favoriteMovies.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    init {
        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        viewModelScope.launch {
            _loadingState.emit(true)

            repository.observeMovies()
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading favorite movies.")
                }
                .collectLatest {
                    _loadingState.emit(false)
                    val movies = it.map { entity -> entity.toMovie() }
                    favoriteMovies.emit(movies)
                }
        }
    }
}