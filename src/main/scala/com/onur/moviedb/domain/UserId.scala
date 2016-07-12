package com.onur.moviedb.domain

import scala.reflect.runtime.universe.TypeTag
import org.http4s.rho.bits.{ResultResponse, FailureResponse, SuccessResponse, StringParser}
import org.http4s.rho.bits.ResponseGeneratorInstances.BadRequest

final case class UserId(id: Long)
object UserId {

  class UserIdParser extends StringParser[UserId] {
    override val typeTag: Some[TypeTag[UserId]] = Some(implicitly[TypeTag[UserId]])
    override def parse(s: String): ResultResponse[UserId] =
      try SuccessResponse(UserId(s.toLong))
      catch { case e: NumberFormatException => FailureResponse.pure { BadRequest.pure(s"Invalid user id format: '$s'") } }
  }

  implicit def userIdParser: UserIdParser = new UserIdParser
}
