package com.onur.moviedb.route.error

import argonaut.Argonaut._
import argonaut.EncodeJson
import com.onur.moviedb.failure.FailureMsg
import org.http4s.argonaut61.ArgonautInstances
import org.http4s.rho.RhoService

import scalaz.NonEmptyList

private[error]
final case class ErrJSONRoot(error: ErrJSON)

// similiar to https://goo.gl/qSTbGa
object ErrJSONRoot {
  implicit def errEncodeJSON: EncodeJson[ErrJSONRoot] =
    jencode1L((e: ErrJSONRoot) => e.error)("error")
}

private[error]
final case class Err(msg: String, reason: String)

object Err {
  implicit def errEncodeJSON: EncodeJson[Err] =
    jencode2L((e: Err) => (e.msg, e.reason))("msg", "reason")
}

private[error]
final case class ErrJSON(code: Int, message: String, errs: NonEmptyList[Err])

private[error] object ErrJSON {
  implicit def errEncodeJSON: EncodeJson[ErrJSON] =
    jencode3L((e: ErrJSON) => (e.code, e.message, e.errs))("code", "message", "errors")
}

object HttpError extends RhoService with ArgonautInstances {
  def s400(errMsg: String, errs: NonEmptyList[FailureMsg]) = BadRequest(root(400, errMsg, errs).asJson)
  def s401(errMsg: String, errs: NonEmptyList[FailureMsg]) = Unauthorized(root(401, errMsg, errs).asJson)
  def s403(errMsg: String, errs: NonEmptyList[FailureMsg]) = Forbidden(root(403, errMsg, errs).asJson)
  def s404(errMsg: String, errs: NonEmptyList[FailureMsg]) = NotFound(root(404, errMsg, errs).asJson)
  def s409(errMsg: String, errs: NonEmptyList[FailureMsg]) = Conflict(root(409, errMsg, errs).asJson)
  def s500(errMsg: String, errs: NonEmptyList[FailureMsg]) = InternalServerError(root(500, errMsg, errs).asJson)
  def s503(errMsg: String, errs: NonEmptyList[FailureMsg]) = ServiceUnavailable(root(503, errMsg, errs).asJson)

  private def root(code: Int, errMsg: String, errs: NonEmptyList[FailureMsg]) =
    ErrJSONRoot (ErrJSON(code, errMsg, errs.map(f => Err(f.failMsg, f.failReason))))

}
