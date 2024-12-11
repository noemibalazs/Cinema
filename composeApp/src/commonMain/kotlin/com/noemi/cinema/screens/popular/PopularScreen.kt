package com.noemi.cinema.screens.popular

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.paging.compose.collectAsLazyPagingItems
import com.noemi.cinema.utils.MovieLazyGrid
import com.noemi.cinema.utils.MovieProgressIndicator
import com.noemi.cinema.utils.NoNetworkConnection
import com.noemi.cinema.utils.showSnackBar
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun PopularScreen(snackBarHostState: SnackbarHostState) {

    val viewModel: PopularViewModel = viewModel { getKoin().get() }
    val scope = rememberCoroutineScope()

    val movies = viewModel.payloadState.collectAsLazyPagingItems()
    val isLoading by viewModel.loadingState.collectAsState()
    val errorMessage by viewModel.errorState.collectAsState()
    val hasNetworkConnection by viewModel.networkState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (hasNetworkConnection) {
            true -> when (isLoading) {
                true -> MovieProgressIndicator()
                else -> MovieLazyGrid(movies = movies, onMovieClicked = viewModel::saveMovie, snackBarHostState = snackBarHostState)
            }
            else -> NoNetworkConnection()
        }

        if (errorMessage.isNotEmpty()) showSnackBar(snackBarHostState = snackBarHostState, message = errorMessage, scope = scope)
    }
}