package com.onur.moviedb.route

import com.onur.moviedb.auth.Login.LoginValidation
import com.onur.moviedb.auth.{SessionModule, SessionService, PasswordEncryption}
import com.onur.moviedb.domain.{SessionToken, IpAddress, Suspended}
import com.onur.moviedb.failure.OperationFailure
import com.onur.moviedb.route.error.{HttpError => HE}
import com.onur.moviedb.user.{UserModule, UserService}
import org.http4s.headers.Authorization
import org.http4s.Request
import org.http4s.argonaut61.ArgonautInstances
import org.http4s.rho.RhoService
import org.http4s.rho._
import org.http4s.util.CaseInsensitiveString
import scalaz.Maybe.Just
import scalaz._

import argonaut.Argonaut._

object AuthenticationRoute {
  lazy val publicRoute = new AuthenticationRoutePublic(UserModule.userService, SessionModule.sessionService).toService()
  lazy val privateRoute = new AuthenticationRoutePrivate(UserModule.userService, SessionModule.sessionService).toService()

  private class AuthenticationRoutePublic (userService: UserService, sessService: SessionService)
    extends RhoService with ArgonautInstances {
    private val loginFailure = "Unable to login"

    POST ^ jsonOf[LoginValidation] |>> { (r: Request, input: LoginValidation) => input match {
      case Success(login) =>  userService.getUserByEmail(login.email).flatMap{
        case Just(user) if user.accountState == Suspended => HE.s400(loginFailure, NonEmptyList(AccountSuspended))
        case Just(user) =>
          PasswordEncryption.verifyPassword(login.pass, user.password) match {
            case \/-(true) =>
              Created(sessService.createSession(user.id,
                Maybe.fromOption(r.headers.get(CaseInsensitiveString("X-Forwarded-For"))).
                  map(h => IpAddress(h.value))).map(_.asJson))
            case _ => HE.s400("Wrong password", NonEmptyList(WrongPassword))
          }
        case _ => HE.s400(loginFailure, NonEmptyList(UserIdNotFound))
      }
      case Failure(errs) => HE.s400(loginFailure, errs)
    }
    }

    private sealed trait UserLoginFailure extends OperationFailure
    private case object UserIdNotFound extends UserLoginFailure {
      override def failMsg: String = "Invalid login id"
    }

    private case object WrongPassword extends UserLoginFailure {
      override def failMsg: String = "Wrong password"
    }

    private case object AccountSuspended extends UserLoginFailure {
      override def failMsg: String = "Account suspended"
    }
  }

  private class AuthenticationRoutePrivate (userService: UserService, sessService: SessionService) extends RhoService {
    DELETE >>> capture(Authorization) |>> { (a: Authorization) => {
      sessService.deleteSession(SessionToken(a.value))
      NoContent()
    }
    }
  }
}
