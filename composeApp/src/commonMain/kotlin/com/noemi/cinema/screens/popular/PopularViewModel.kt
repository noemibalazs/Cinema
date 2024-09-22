package com.noemi.cinema.screens.popular

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.base.BaseViewModel
import com.noemi.cinema.model.Movie
import com.noemi.cinema.paging.config.PopularPagingConfig
import com.noemi.cinema.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PopularViewModel(
    konnectivity: Konnectivity,
    repository: MovieRepository,
    private val popularPagingConfig: PopularPagingConfig
) : BaseViewModel<PagingData<Movie>>(konnectivity, repository) {

    private val _popularPagingMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    override val payloadState: StateFlow<PagingData<Movie>> = _popularPagingMovies.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    init {
        loadPopularMovies()
    }

    private fun loadPopularMovies() {
        viewModelScope.launch {
            _loadingState.emit(true)

            popularPagingConfig.loadMovies()
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading most popular movies.")
                }
                .cachedIn(this)
                .collectLatest {
                    _loadingState.emit(false)
                    _popularPagingMovies.emit(it)
                }
        }
    }
}