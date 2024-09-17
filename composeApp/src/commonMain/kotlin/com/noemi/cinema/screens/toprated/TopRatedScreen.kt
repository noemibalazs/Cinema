package com.noemi.cinema.screens.toprated

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
import app.cash.paging.compose.collectAsLazyPagingItems
import com.noemi.cinema.utils.MovieLazyGrid
import com.noemi.cinema.utils.MovieProgressIndicator
import com.noemi.cinema.utils.showSnackBar
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun TopRatedScreen(snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {

    val viewModel: TopRatedViewModel = viewModel { getKoin().get() }
    val scope = rememberCoroutineScope()

    val movies = viewModel.payloadState.collectAsLazyPagingItems()
    val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (isLoading) {
            true -> MovieProgressIndicator()
            else -> MovieLazyGrid(
                movies = movies,
                onMovieClicked = viewModel::saveMovie,
                snackBarHostState = snackBarHostState
            )
        }

        if (errorMessage.isNotBlank()) {
            showSnackBar(snackBarHostState = snackBarHostState, message = errorMessage, scope = scope)
        }
    }
}