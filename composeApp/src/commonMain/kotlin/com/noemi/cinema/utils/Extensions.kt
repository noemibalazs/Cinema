package com.noemi.cinema.utils

import com.noemi.cinema.database.MovieEntity
import com.noemi.cinema.model.Movie
import com.noemi.cinema.utils.Constants.POSTER_URL
import com.noemi.cinema.utils.Constants.YOUTUBE_END
import com.noemi.cinema.utils.Constants.YOUTUBE_PATH
import com.noemi.cinema.utils.Constants.YOUTUBE_START

//fun toMovieDetails(movieId: Int) {
//    val intent = Intent(this, MovieDetailsActivity::class.java)
//    val bundle = Bundle()
//    bundle.putInt(KEY_MOVIE_ID, movieId)
//    intent.putExtras(bundle)
//    startActivity(intent)
//}

fun Movie.toEntity(placeHolder: String): MovieEntity =
    MovieEntity(id = id, title = title, description = description, releaseDate = releaseDate, rating = rating, posterPath = getPoster(placeHolder))

fun Movie.getPoster(placeholder: String): String = when (posterPath == null) {
    true -> placeholder
    else -> posterPath.getMoviePoster()
}

fun String.getMoviePoster(): String {
    return POSTER_URL + this
}

fun MovieEntity.toMovie(): Movie {
    val path = when (posterPath.startsWith(POSTER_URL)) {
        true -> posterPath.drop(31)
        else -> posterPath
    }
    return Movie(
        id = id, title = title, description = description, releaseDate = releaseDate, rating = rating, posterPath = path
    )
}

fun String.getMovieYoutubePath(): String {
    return YOUTUBE_PATH + this
}

fun String.getYoutubeScreenShot(): String {
    return YOUTUBE_START + this + YOUTUBE_END
}