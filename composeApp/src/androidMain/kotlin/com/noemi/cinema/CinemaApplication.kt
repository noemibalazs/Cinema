package com.noemi.cinema

import android.app.Application
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import com.noemi.cinema.di.appModule
import com.noemi.cinema.di.platformModule
import com.noemi.cinema.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class CinemaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CinemaApplication)
            logger(
                KermitKoinLogger(Logger.withTag("Koin"))
            )

            modules(
                platformModule(),
                serviceModule(),
                appModule()
            )
        }
    }
}