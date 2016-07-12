package com.onur.moviedb.route

import argonaut.Argonaut._
import com.onur.moviedb.domain.Movie.MovieValidation
import com.onur.moviedb.route.error.{HttpError => HE}
import com.onur.moviedb.movie.{MovieModule, MovieService}
import org.http4s.argonaut61.ArgonautInstances
import org.http4s.rho.{RhoService, _}

import scalaz.{Failure, Success}

object MovieRoute {
  lazy val route = new MovieRoute(MovieModule.movieService).toService()
}

private class MovieRoute(movieService: MovieService) extends RhoService with ArgonautInstances {

  POST ^ jsonOf[MovieValidation] |>>  {input: MovieValidation =>
    input match {
      case Success(movie) => Created(movieService.addMovie(movie).map(_.asJson))
      case Failure(errs) => HE.s400("Invalid movie details", errs)
    }
  }

  GET |>> movieService.getMovies.map(_.asJson)

}

