package com.noemi.cinema.di

import com.noemi.cinema.database.MovieDataBase
import com.noemi.cinema.room.getDatabase
import com.noemi.cinema.utils.Constants.BASE_URL
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

actual fun platformModule(): Module = module {
    single<MovieDataBase> { getDatabase(get()) }
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
        HttpClient(Android) {

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
        }
    }
}