package com.noemi.cinema.di

import com.mirego.konnectivity.Konnectivity
import com.noemi.cinema.database.MovieDataBase
import com.noemi.cinema.paging.config.PopularPagingConfig
import com.noemi.cinema.paging.config.TopRatedPagingConfig
import com.noemi.cinema.paging.source.PopularPagingSource
import com.noemi.cinema.paging.source.TopRatedPagingSource
import com.noemi.cinema.repository.MovieRepository
import com.noemi.cinema.repository.MovieRepositoryImpl
import com.noemi.cinema.screens.details.MovieDetailsViewModel
import com.noemi.cinema.screens.favorite.FavoriteViewModel
import com.noemi.cinema.screens.main.MovieViewModel
import com.noemi.cinema.screens.popular.PopularViewModel
import com.noemi.cinema.screens.toprated.TopRatedViewModel
import com.noemi.cinema.service.MovieService
import com.noemi.cinema.service.MovieServiceImpl
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

expect fun serviceModule(): Module

fun appModule() = module {

    single<MovieService> {
        val client: HttpClient = get()
        MovieServiceImpl(client)
    }
    single<CoroutineDispatcher> { Dispatchers.IO }

    single { PopularPagingSource(get()) }
    single { TopRatedPagingSource(get()) }

    single { PopularPagingConfig(get(), get()) }
    single { TopRatedPagingConfig(get(), get()) }

    single<MovieRepository> {
        val dataBase: MovieDataBase = get()
        MovieRepositoryImpl(
            service = get(),
            movieDAO = dataBase.getMovieDao(),
            dispatcher = get()
        )
    }

    factory { TopRatedViewModel(get(), get(), get()) }
    factory { PopularViewModel(get(), get(), get()) }
    factory { FavoriteViewModel(get(), get()) }
    factory { MovieDetailsViewModel(get(), get()) }
    factory { MovieViewModel(get(), get()) }

    single { Konnectivity() }
}
