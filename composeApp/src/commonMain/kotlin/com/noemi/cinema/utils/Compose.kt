package com.noemi.cinema.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.compose.LazyPagingItems
import cinema.composeapp.generated.resources.Res
import cinema.composeapp.generated.resources.label_lazy_column_tag
import cinema.composeapp.generated.resources.label_movie_avatar
import cinema.composeapp.generated.resources.label_movie_item_tag
import cinema.composeapp.generated.resources.label_progress_indicator_tag
import cinema.composeapp.generated.resources.no_internet
import coil3.compose.AsyncImage
import com.noemi.cinema.database.MovieEntity
import com.noemi.cinema.model.Movie
import com.noemi.cinema.screens.details.MovieDetailsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun showSnackBar(snackBarHostState: SnackbarHostState, message: String, scope: CoroutineScope) {
    scope.launch {
        snackBarHostState.showSnackbar(
            duration = SnackbarDuration.Short,
            message = message
        )
    }
}

@Composable
fun NoNetworkConnection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.no_internet),
            contentDescription = null,
            modifier = modifier.size(210.dp)
        )
    }
}

@Composable
fun MovieProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .testTag(stringResource(Res.string.label_progress_indicator_tag)),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        strokeWidth = 3.dp
    )
}

@Composable
fun MovieGrid(
    movies: List<Movie>,
    onMovieClicked: (MovieEntity) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        content = {
            items(
                count = movies.size,
                key = { index -> movies[index].id }
            ) { index ->
                MovieItem(movie = movies[index], onMovieClicked = onMovieClicked, snackBarHostState = snackBarHostState)
            }
        }
    )
}

@Composable
fun MovieLazyGrid(
    movies: LazyPagingItems<Movie>,
    onMovieClicked: (MovieEntity) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag(stringResource(Res.string.label_lazy_column_tag)),
        content = {
            items(
                count = movies.itemCount
            ) { index ->
                movies[index]?.let { movie ->
                    MovieItem(movie = movie, onMovieClicked = onMovieClicked, snackBarHostState = snackBarHostState)
                }
            }
        }
    )
}

@Composable
fun MovieItem(
    movie: Movie,
    onMovieClicked: (MovieEntity) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var onItemClicked by remember { mutableStateOf(false) }
    val onDismissRequest: (Boolean) -> Unit = { changed ->
        onItemClicked = changed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable {
                onMovieClicked
                    .invoke(movie.toEntity())
                    .also {
                        onItemClicked = true
                    }
            }
            .testTag(stringResource(Res.string.label_movie_item_tag)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = movie.getPoster(),
            contentDescription = stringResource(Res.string.label_movie_avatar),
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    if (onItemClicked) {
        MovieDetailsDialog(snackBarHostState = snackBarHostState, movieId = movie.id, onDismissRequest = onDismissRequest)
    }
}

@Composable
fun MovieHeader(id: StringResource, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier.padding(top = 20.dp),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
}
