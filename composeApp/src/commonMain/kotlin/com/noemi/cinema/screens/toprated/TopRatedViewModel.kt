package com.noemi.cinema.screens.toprated

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.base.BaseViewModel
import com.noemi.cinema.model.Movie
import com.noemi.cinema.paging.config.TopRatedPagingConfig
import com.noemi.cinema.repository.MovieRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopRatedViewModel(
    konnectivity: Konnectivity,
    repository: MovieRepository,
    private val topRatedPagingConfig: TopRatedPagingConfig
) : BaseViewModel<PagingData<Movie>>(konnectivity, repository) {

    private val _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    private val _topRatedPagingMovie = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    override val payloadState: StateFlow<PagingData<Movie>> = _topRatedPagingMovie.asStateFlow()

    init {
        loadTopRatedMovies()
    }

    private fun loadTopRatedMovies() {
        viewModelScope.launch {

            _loadingState.emit(true)

            delay(1200)

            topRatedPagingConfig.loadMovies()
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading top rated movies")
                }
                .cachedIn(this)
                .collectLatest {
                    _loadingState.emit(false)
                    _topRatedPagingMovie.emit(it)
                }
        }
    }
}