package com.noemi.cinema.room

import androidx.room.Room
import androidx.room.RoomDatabase
import com.noemi.cinema.database.MovieDataBase
import com.noemi.cinema.database.instantiateImpl
import com.noemi.cinema.utils.Constants.MOVIE_DB
import platform.Foundation.NSHomeDirectory
import androidx.sqlite.driver.bundled.BundledSQLiteDriver


fun getDatabase(): MovieDataBase {
    return getDatabaseBuilder().build()
}

fun getDatabaseBuilder(): RoomDatabase.Builder<MovieDataBase> {
    val path = "${NSHomeDirectory()}/${MOVIE_DB}"
    return Room.databaseBuilder<MovieDataBase>(
        name = path,
        factory = { MovieDataBase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver())
}