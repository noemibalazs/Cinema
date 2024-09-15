package com.noemi.cinema.pager

import cinema.composeapp.generated.resources.Res
import cinema.composeapp.generated.resources.label_favorite
import cinema.composeapp.generated.resources.label_popular
import cinema.composeapp.generated.resources.label_top_rated
import org.jetbrains.compose.resources.StringResource

enum class TabItem(val tab: StringResource) {
    TOP_RATED(tab = Res.string.label_top_rated),
    POPULAR(tab = Res.string.label_popular),
    FAVORITE(tab = Res.string.label_favorite);

    companion object {
        fun getMovieTabs() = listOf(TOP_RATED, POPULAR, FAVORITE)
    }
}