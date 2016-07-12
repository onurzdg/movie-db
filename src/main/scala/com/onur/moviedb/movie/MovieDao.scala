package com.onur.moviedb.movie


import com.onur.moviedb.domain._
import doobie.imports._

import scalaz.Maybe.Just


trait MovieDao {
  def addMovie(m: Movie[Validated]): ConnectionIO[Movie[Validated]] =
      sql"""insert into movie(title, year, imdbRating)
         values(${m.title},${m.year},${m.imdbRating})""".update.withUniqueGeneratedKeys[Long]("id").map{mid: Long =>
        m.copy(id = Just(MovieId(mid)))
      }

  def getMovies: ConnectionIO [List[Movie[Validated]]] =
    sql"select * from movie)".query[Movie[Validated]].list
}

