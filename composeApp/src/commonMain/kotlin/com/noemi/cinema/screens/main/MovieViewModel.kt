package com.noemi.cinema.screens.main

import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.base.BaseViewModel
import com.noemi.cinema.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MovieViewModel(
    konnectivity: Konnectivity,
    repository: MovieRepository
) : BaseViewModel<Unit>(konnectivity, repository) {

    override val loadingState: StateFlow<Boolean> = MutableStateFlow(false)
    override val errorState: StateFlow<String> = MutableStateFlow("")
    override val payloadState: StateFlow<Unit> = MutableStateFlow(Unit)
}