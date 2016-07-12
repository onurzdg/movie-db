package com.onur.moviedb.movie

import com.onur.moviedb.db.DB
import com.onur.moviedb.domain.{Validated, Movie}
import doobie.imports._

import scalaz.concurrent.Task


class MovieService(dao: MovieDao) {
  import com.onur.moviedb.concurrency.ExecutionContext.dbContext

  def addMovie(m: Movie[Validated]): Task[Movie[Validated]] =
    Task.fork(dao.addMovie(m).transact(DB.xa))

  def getMovies: Task[List[Movie[Validated]]] =
    Task.fork(dao.getMovies.transact(DB.xa))
}
