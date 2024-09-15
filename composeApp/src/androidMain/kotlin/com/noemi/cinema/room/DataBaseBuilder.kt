package com.noemi.cinema.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.noemi.cinema.database.MovieDataBase
import com.noemi.cinema.utils.Constants.MOVIE_DB

fun getDatabase(context: Context): MovieDataBase {
    return getDatabaseBuilder(context).build()
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<MovieDataBase> {
    val appContext = context.applicationContext
    val filePath = appContext.getDatabasePath(MOVIE_DB)
    return Room.databaseBuilder<MovieDataBase>(
        appContext,
        filePath.absolutePath
    ).setDriver(BundledSQLiteDriver())
}