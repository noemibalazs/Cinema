package com.noemi.cinema.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noemi.cinema.model.Movie
import com.noemi.cinema.service.MovieService

class PopularPagingSource(private val service: MovieService) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val position = params.key ?: 1
            val response = service.loadPopularMovies(position)

            when (response.movies.isNotEmpty()) {
                true -> LoadResult.Page(
                    data = response.movies,
                    prevKey = if (position == 1) null else position.minus(1),
                    nextKey = if (position == response.totalPages) null else position.plus(1)
                )

                else -> LoadResult.Error(Throwable("No popular movie results found."))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}