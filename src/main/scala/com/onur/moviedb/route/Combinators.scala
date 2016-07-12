package com.onur.moviedb.route

import com.onur.moviedb.domain.UserId
import org.http4s.rho._
import org.http4s.rho.bits.{FailureResponse, SuccessResponse, TypedHeader}
import org.http4s.util.CaseInsensitiveString
import shapeless.{HNil, ::}


object Combinators {

  def requireUserId: TypedHeader[::[UserId, HNil]] = genericHeaderCapture(h => {
    h.get(CaseInsensitiveString("userId")) match {
      case Some(a) => SuccessResponse(UserId(a.value.toLong))
      case _ => FailureResponse.badRequest("missing id")
    }
  })

}
