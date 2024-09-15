package com.noemi.cinema.paging.config

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.noemi.cinema.model.Movie
import com.noemi.cinema.paging.MoviePaging
import com.noemi.cinema.paging.source.PopularPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class PopularPagingConfig(
    private val popularPagingSource: PopularPagingSource,
    private val dispatcher: CoroutineDispatcher
) : MoviePaging {

    override fun loadMovies(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 30),
        pagingSourceFactory = { popularPagingSource }
    ).flow.flowOn(dispatcher)
}