package com.onur.moviedb.auth


trait SessionModule {
  lazy val dao = new SessionDAO {}
  lazy val sessionService = new SessionService(dao)
}

object SessionModule extends SessionModule
