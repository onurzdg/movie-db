package com.onur.moviedb.route

import argonaut.Argonaut._
import com.onur.moviedb.domain._
import com.onur.moviedb.route.error.{HttpError => HE}
import com.onur.moviedb.user.UserCreation.UserCreationValidation
import com.onur.moviedb.user.{UserModule, UserService}
import org.http4s.Request
import org.http4s.argonaut61.ArgonautInstances
import org.http4s.rho.{RhoService, _}

import scalaz.{-\/, Failure, NonEmptyList, Success, \/-}

object UserRoute {
  lazy val route = new UserRoute(UserModule.userService).toService()
}

private class UserRoute private(userService: UserService) extends RhoService with ArgonautInstances {

  GET |>> Ok(userService.getUsers.map(_.asJson))

  GET / pathVar[UserId] |>> { (uid: UserId) =>
    Ok(userService.getUserById(uid).map(_.asJson))
  }

  POST ^ jsonOf[UserCreationValidation] |>> { (r: Request, input: UserCreationValidation) =>
    input match {
      case Success(user) =>
        userService.createUser(user).flatMap {
          case \/-(res) => Created(res.asJson)
          case -\/(err) => HE.s409("Email is already registered", NonEmptyList(err))
        }
      case Failure(errs) => HE.s400("Invalid account details", errs)
    }
  }
}


