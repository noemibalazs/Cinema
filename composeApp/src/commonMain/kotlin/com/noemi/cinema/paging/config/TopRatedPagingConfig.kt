package com.noemi.cinema.paging.config

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.noemi.cinema.model.Movie
import com.noemi.cinema.paging.MoviePaging
import com.noemi.cinema.paging.source.TopRatedPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class TopRatedPagingConfig(
    private val topRatedPagingSource: TopRatedPagingSource,
    private val dispatcher: CoroutineDispatcher
) : MoviePaging {

    override fun loadMovies(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(10, 30),
        pagingSourceFactory = { topRatedPagingSource }
    ).flow.flowOn(dispatcher)
}