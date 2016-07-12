package com.onur.moviedb.domain

import argonaut.Argonaut._
import argonaut.CodecJson
import org.http4s.rho.bits.ResponseGeneratorInstances.BadRequest
import org.http4s.rho.bits.{FailureResponse, SuccessResponse, ResultResponse, StringParser}

import scala.reflect.runtime.universe.TypeTag


final case class MovieId(id: Long)

object MovieId {
  class UserIdParser extends StringParser[MovieId] {
    override val typeTag: Some[TypeTag[MovieId]] = Some(implicitly[TypeTag[MovieId]])
    override def parse(s: String): ResultResponse[MovieId] =
      try SuccessResponse(MovieId(s.toLong))
      catch { case e: NumberFormatException => FailureResponse.pure { BadRequest.pure(s"Invalid user id format: '$s'") } }
  }
  implicit def movieIdCodecJson: CodecJson[MovieId] = casecodec1(MovieId.apply, MovieId.unapply)("movieId")
}
