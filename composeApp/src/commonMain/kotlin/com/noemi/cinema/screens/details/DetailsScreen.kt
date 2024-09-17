package com.noemi.cinema.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.viewmodel.compose.viewModel
import cinema.composeapp.generated.resources.Res
import cinema.composeapp.generated.resources.label_movie_container_tag
import cinema.composeapp.generated.resources.label_movie_content_tag
import cinema.composeapp.generated.resources.label_movie_screenshot_tag
import cinema.composeapp.generated.resources.label_movie_title_tag
import cinema.composeapp.generated.resources.label_trailers
import coil3.compose.AsyncImage
import com.noemi.cinema.model.Movie
import com.noemi.cinema.model.Review
import com.noemi.cinema.model.Trailer
import com.noemi.cinema.utils.MovieHeader
import com.noemi.cinema.utils.MovieProgressIndicator
import com.noemi.cinema.utils.getPoster
import com.noemi.cinema.utils.showSnackBar
import kotlinx.coroutines.launch
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import cinema.composeapp.generated.resources.down
import cinema.composeapp.generated.resources.label_icon_down_tag
import cinema.composeapp.generated.resources.label_movie_trailer
import cinema.composeapp.generated.resources.label_reviews
import cinema.composeapp.generated.resources.label_summary
import cinema.composeapp.generated.resources.label_summary_tag
import cinema.composeapp.generated.resources.label_toast_message
import cinema.composeapp.generated.resources.placeholder
import cinema.composeapp.generated.resources.up
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.noemi.cinema.utils.getMovieYoutubePath
import com.noemi.cinema.utils.getYoutubeScreenShot
import org.jetbrains.compose.resources.painterResource

@Composable
fun MovieDetailsDialog(snackBarHostState: SnackbarHostState, onDismissRequest: (Boolean) -> Unit, movieId: Int) {

    Dialog(onDismissRequest = { onDismissRequest.invoke(false) }) {
        DetailsScreen(snackBarHostState, movieId)
    }
}

@Composable
fun DetailsScreen(snackBarHostState: SnackbarHostState, movieId: Int, modifier: Modifier = Modifier) {

    val viewModel: MovieDetailsViewModel = viewModel { getKoin().get() }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val movie by viewModel.payloadState.collectAsStateWithLifecycle()
    val reviews by viewModel.reviewsState.collectAsStateWithLifecycle()
    val trailers by viewModel.trailersState.collectAsStateWithLifecycle()
    val hasConnection by viewModel.networkState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = movieId) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.monitorNetworkState(scope)
            scope.launch { viewModel.loadMovieDetails(movieId) }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (isLoading) {
            true -> MovieProgressIndicator()
            else -> MotionContainer(
                movie = movie,
                trailers = trailers,
                reviews = reviews,
                activeNetwork = hasConnection,
                movieRating = viewModel::getMovieRating,
                snackBarHostState = snackBarHostState
            )
        }

        if (errorMessage.isNotEmpty()) {
            showSnackBar(snackBarHostState = snackBarHostState, message = errorMessage, scope = scope)
        }
    }
}

@Composable
fun MotionContainer(
    movie: Movie,
    trailers: List<Trailer>,
    reviews: List<Review>,
    activeNetwork: Boolean,
    snackBarHostState: SnackbarHostState,
    movieRating: () -> String,
    modifier: Modifier = Modifier
) {
    val big = 270.dp
    val small = 90.dp

    val scene = MotionScene {
        val image = createRefFor("headerImage")
        val title = createRefFor("title")

        val start1 = constraintSet {
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(big)
                top.linkTo(parent.top, 0.dp)
            }
            constrain(title) {
                start.linkTo(image.end, 40.dp)
            }
        }

        val end1 = constraintSet {
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(small)
                top.linkTo(parent.top)
            }
            constrain(title) {
                bottom.linkTo(image.bottom)
                start.linkTo(image.start, 20.dp)

                scaleX = 0.7f
                scaleY = 0.7f
                pivotX = 0f
            }
        }
        transition(start1, end1, "default") {}
    }

    val minHeight = with(LocalDensity.current) { small.roundToPx().toFloat() }
    val maxHeight = with(LocalDensity.current) { big.roundToPx().toFloat() }

    val toolbarHeight = remember { mutableStateOf(maxHeight) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = toolbarHeight.value

                if (height + available.y > maxHeight) {
                    toolbarHeight.value = maxHeight
                    return Offset(0f, maxHeight - height)
                }

                if (height + available.y < minHeight) {
                    toolbarHeight.value = minHeight
                    return Offset(0f, minHeight - height)
                }

                toolbarHeight.value += available.y
                return Offset(0f, available.y)
            }
        }
    }

    val progress = 1 - (toolbarHeight.value - minHeight) / (maxHeight - minHeight)

    Column(modifier = modifier.testTag(stringResource(Res.string.label_movie_container_tag))) {
        MotionLayout(
            motionScene = scene,
            progress = progress
        ) {

            AsyncImage(
                model = movie.getPoster(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .layoutId("headerImage")
                    .testTag(stringResource(Res.string.label_movie_screenshot_tag)),
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .layoutId("title")
                    .testTag(stringResource(Res.string.label_movie_title_tag)),
                text = movie.title,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        MovieContent(
            movie = movie,
            trailers = trailers,
            reviews = reviews,
            activeNetwork = activeNetwork,
            scrollConnection = nestedScrollConnection,
            snackBarHostState = snackBarHostState,
            movieRating = movieRating.invoke()
        )
    }
}

@Composable
fun MovieContent(
    movie: Movie,
    trailers: List<Trailer>,
    reviews: List<Review>,
    activeNetwork: Boolean,
    scrollConnection: NestedScrollConnection,
    snackBarHostState: SnackbarHostState,
    movieRating: String,
    modifier: Modifier = Modifier
) {

    val lazyState = rememberLazyListState()
    val size = trailers.size + reviews.size + 3

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .nestedScroll(scrollConnection)
            .testTag(stringResource(Res.string.label_movie_content_tag)),
        state = lazyState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {

        item {
            MovieShortDescription(
                movie = movie,
                state = lazyState,
                size = size,
                movieRating = movieRating
            )
        }

        item {
            MovieHeader(Res.string.label_trailers)
        }

        items(
            items = trailers,
            key = { trailer -> trailer.key }
        ) { trailer ->
            MovieTrailer(trailer = trailer, hasNetwork = activeNetwork, snackBarHostState = snackBarHostState)
        }

        item {
            MovieHeader(Res.string.label_reviews)
        }

        items(
            items = reviews,
            key = { review -> review.author }
        ) { review ->
            MovieReview(review = review)
        }

        item {
            ContentScrollUp(state = lazyState)
        }
    }
}

@Composable
fun MovieShortDescription(
    movie: Movie,
    state: LazyListState,
    size: Int,
    movieRating: String,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(Res.string.label_summary),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.testTag(stringResource(Res.string.label_summary_tag)),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            )

            Icon(
                painter = painterResource(Res.drawable.down),
                contentDescription = null,
                modifier = modifier
                    .size(42.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        scope.launch {
                            state.scrollToItem(size)
                        }
                    }
                    .testTag(stringResource(Res.string.label_icon_down_tag))
            )
        }

        Text(
            text = movie.description,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal
        )

        Text(
            text = movie.releaseDate,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .align(Alignment.End)
                .padding(top = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic
        )

        Text(
            text = movieRating,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .align(Alignment.End),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun MovieTrailer(trailer: Trailer, hasNetwork: Boolean, snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val message = stringResource(Res.string.label_toast_message)

    var isClicked by remember { mutableStateOf(false) }
    val onTrailerDismiss: (Boolean) -> Unit = { dismiss ->
        isClicked = dismiss
    }

    Column(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(top = 12.dp)
                .clickable {

                    when (hasNetwork) {
                        true -> isClicked = true
                        else -> showSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = trailer.key.getYoutubeScreenShot(),
                contentDescription = stringResource(Res.string.label_movie_trailer),
                placeholder = painterResource(Res.drawable.placeholder),
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(Res.drawable.placeholder)
            )
        }


        Text(
            text = trailer.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier.padding(top = 12.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal
        )
    }

    if (isClicked) {
        CinemaWebView(url = trailer.key.getMovieYoutubePath(), onTrailerDismiss = onTrailerDismiss)
    }
}

@Composable
fun MovieReview(review: Review, modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = review.author,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier.padding(top = 12.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Normal
        )

        Text(
            text = review.content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(top = 6.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal
        )
    }
}

@Composable
fun ContentScrollUp(
    state: LazyListState, modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(Res.drawable.up),
            contentDescription = null,
            modifier = modifier
                .size(42.dp)
                .align(Alignment.CenterVertically)
                .clickable {
                    scope.launch {
                        state.scrollToItem(0)
                    }
                }
        )
    }
}

@Composable
private fun CinemaWebView(url: String, onTrailerDismiss: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = { onTrailerDismiss.invoke(false) }) {

        val webViewState = rememberWebViewState(url)

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { }

        WebView(
            state = webViewState,
            modifier = Modifier.fillMaxSize()
        )
    }
}