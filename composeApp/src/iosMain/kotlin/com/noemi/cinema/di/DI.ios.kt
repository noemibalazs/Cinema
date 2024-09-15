package com.noemi.cinema.di

import com.noemi.cinema.database.MovieDataBase
import com.noemi.cinema.room.getDatabase
import com.noemi.cinema.utils.Constants.BASE_URL
import com.noemi.cinema.utils.Constants.TIME_OUT_MILLIS
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single<MovieDataBase> { getDatabase() }
}

actual fun serviceModule(): Module = module {

    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = false
        }
    }

    single {
        HttpClient(Darwin) {

            defaultRequest {
                url(BASE_URL)
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
                logger = object : Logger {
                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.d(message)
                    }
                }
            }

            install(ContentNegotiation) {
                json(get())
            }

            install(HttpTimeout) {
                requestTimeoutMillis = TIME_OUT_MILLIS
                connectTimeoutMillis = TIME_OUT_MILLIS
            }
        }
    }
}