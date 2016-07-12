package com.onur.moviedb.movie


trait MovieModule {
  lazy val dao = new MovieDao {}
  lazy val movieService = new MovieService(dao)
}

object MovieModule extends MovieModule
