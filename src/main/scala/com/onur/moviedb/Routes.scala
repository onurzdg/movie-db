package com.onur.moviedb


import com.onur.moviedb.route.{MovieRoute, AuthenticationRoute, UserRoute}
import org.http4s.HttpService
import org.http4s.server.Router

class Routes {
  val privateRoutes: HttpService = Router(
    "/movie" -> MovieRoute.route,
    "/user" -> UserRoute.route,
    "/auth" -> AuthenticationRoute.privateRoute
  )

  val publicRoutes: HttpService = Router(
    "/auth" -> AuthenticationRoute.publicRoute
  )
}
