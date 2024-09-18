package com.noemi.cinema.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cinema.composeapp.generated.resources.Res
import cinema.composeapp.generated.resources.label_pager_tag
import cinema.composeapp.generated.resources.label_tab_row_tag
import com.noemi.cinema.pager.TabItem
import com.noemi.cinema.screens.favorite.FavoriteScreen
import com.noemi.cinema.screens.popular.PopularScreen
import com.noemi.cinema.screens.toprated.TopRatedScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesApp(snackBarHostState: SnackbarHostState) {

    val tabs = TabItem.getMovieTabs().map { it.tab }
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        MovieTabLayout(tabs = tabs, pagerState = pagerState)

        MovieTabContent(pagerState = pagerState, snackBarHostState = snackBarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MovieTabLayout(tabs: List<StringResource>, pagerState: PagerState, modifier: Modifier = Modifier) {

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)){
        Spacer(modifier = modifier.height(42.dp))
    }

    PrimaryTabRow(
        selectedTabIndex = tabIndex,
        modifier = modifier
            .fillMaxWidth()
            .testTag(stringResource(Res.string.label_tab_row_tag)),
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        indicator = {
            MovieIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.tabIndicatorOffset(tabIndex)
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabIndex == index,
                unselectedContentColor = Color.White,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun MovieIndicator(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(6.dp)
            .fillMaxSize()
            .border(
                border = BorderStroke(2.dp, color),
                shape = RoundedCornerShape(6.dp)
            )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieTabContent(snackBarHostState: SnackbarHostState, pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .testTag(stringResource(Res.string.label_pager_tag))
    ) { index ->
        when (index) {
            0 -> TopRatedScreen(snackBarHostState = snackBarHostState)
            1 -> PopularScreen(snackBarHostState = snackBarHostState)
            else -> FavoriteScreen(snackBarHostState = snackBarHostState)
        }
    }
}