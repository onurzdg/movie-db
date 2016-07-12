package com.onur.moviedb

import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext


object StaticService {
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  def service(): HttpService = Router(
    "" -> rootService
  )

  def rootService() = HttpService {
    case req@GET -> Root =>
      Ok("ok")

    case _ -> Root =>
      MethodNotAllowed()

    case GET -> Root / "ping" =>
      Ok("pong")

  }
}
