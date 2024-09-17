package com.noemi.cinema.screens.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noemi.cinema.utils.MovieGrid
import com.noemi.cinema.utils.MovieProgressIndicator
import com.noemi.cinema.utils.showSnackBar
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.koin.mp.KoinPlatform.getKoin


@Composable
fun FavoriteScreen(snackBarHostState: SnackbarHostState) {

    val viewModel: FavoriteViewModel = viewModel { getKoin().get() }
    val scope = rememberCoroutineScope()

    val movies by viewModel.payloadState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (isLoading) {
            true -> MovieProgressIndicator()
            else -> MovieGrid(movies = movies, onMovieClicked = viewModel::saveMovie, snackBarHostState = snackBarHostState)
        }

        if (errorMessage.isNotEmpty()) {
            showSnackBar(snackBarHostState = snackBarHostState, message = errorMessage, scope = scope)
        }
    }
}