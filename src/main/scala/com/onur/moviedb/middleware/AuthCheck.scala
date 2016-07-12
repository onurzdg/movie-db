package com.onur.moviedb.middleware

import com.onur.moviedb.auth.SessionService
import com.onur.moviedb.domain.{UserId, SessionToken}
import org.http4s._
import org.http4s.headers.Authorization

import scalaz.Maybe.Just
import scalaz.concurrent.Task


object AuthCheck {
  private trait AuthReply
  final private case class OK(uid: UserId) extends AuthReply
  private case object NeedsAuth extends AuthReply


  def apply(sessService: SessionService, service: HttpService): HttpService = Service.lift {req =>
    checkAuth(req, sessService).flatMap{
      case OK(uid) =>
        req.putHeaders(Header("userId", uid.id.toString))
        service(req)
      case NeedsAuth => Task.now(Response(Status.Unauthorized))
    }
  }

  private def checkAuth(req: Request, sessService: SessionService): Task[AuthReply] =  {
    req.headers.get(Authorization) match {
      case Some(x) => sessService.getSessionByToken(SessionToken(x.value)).flatMap{
        case Just(sess) => Task.now(OK(sess.uid))
        case _ => Task.now(NeedsAuth)
      }
      case _ => Task.now(NeedsAuth)
    }
  }
}
