package com.noemi.cinema.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.mirego.konnectivity.NetworkState
import com.noemi.cinema.database.MovieEntity
import com.noemi.cinema.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseViewModel<T : Any>(
    private val konnectivity: Konnectivity,
    private val repository: MovieRepository
) : ViewModel() {

    abstract val loadingState: StateFlow<Boolean>
    abstract val errorState: StateFlow<String>
    abstract val payloadState: StateFlow<T>

    private var _networkState = MutableStateFlow(false)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    fun monitorNetworkState(scope: CoroutineScope) {
        konnectivity.networkState
            .onEach { networkState ->
                when (networkState) {
                    NetworkState.Unreachable -> onNetworkStateChanged(false)
                    else -> onNetworkStateChanged(true)
                }.also {
                    println("Network state is active: $networkState")
                }
            }.launchIn(scope)
    }

    private fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
        }
    }

    fun saveMovie(entity: MovieEntity) {
        viewModelScope.launch {
            repository.insertMovie(entity)
        }
    }
}