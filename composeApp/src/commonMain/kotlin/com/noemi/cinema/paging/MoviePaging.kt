package com.noemi.cinema.paging

import androidx.paging.PagingData
import com.noemi.cinema.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviePaging {

    fun loadMovies(): Flow<PagingData<Movie>>
}