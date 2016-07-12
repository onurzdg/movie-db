package com.onur.moviedb

import javax.servlet.Servlet

import com.onur.moviedb.auth.SessionModule
import com.onur.moviedb.middleware.AuthCheck
import org.http4s.servlet.{Http4sServlet, NonBlockingServletIo}

import scala.concurrent.duration._
import scalaz.concurrent.Strategy

import org.http4s.server.middleware.GZip

object ServBuilder {
  def staticAPI: Servlet = new Http4sServlet(
    GZip(StaticService.service()),
    30.seconds,
    Strategy.DefaultExecutorService,
    NonBlockingServletIo.apply(4096)
  )

  def privateAPI : Servlet = new Http4sServlet(
    AuthCheck(SessionModule.sessionService, GZip(new Routes().privateRoutes)),
    30.seconds,
    Strategy.DefaultExecutorService,
    NonBlockingServletIo.apply(4096)
  )

  def publicAPI : Servlet = new Http4sServlet(
    GZip(new Routes().publicRoutes),
    30.seconds,
    Strategy.DefaultExecutorService,
    NonBlockingServletIo.apply(4096)
  )
}
