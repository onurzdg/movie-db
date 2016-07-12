package com.onur.moviedb.auth

import com.onur.moviedb.domain.{UnValidated, Validated, _}
import com.onur.moviedb.failure.ValidationFailure

import scalaz.ValidationNel
import scalaz.syntax.applicative._

final case class Login[S <: ValidationStatus](email: Email[S], pass: ClearTextPassword[S])

object Login {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  type LoginValidation = ValidationNel[ValidationFailure, Login[Validated]]

  implicit def personDecodeJson: DecodeJson[LoginValidation] =
    DecodeJson(c =>
      ( c.as[Email[UnValidated]] |@|
        c.as[ClearTextPassword[UnValidated]]
      ) ((e, p) => validate(Login[UnValidated](e, p)))
    )

  private def validate(l: Login[UnValidated]): LoginValidation =
    (l.email.validationNel |@| l.pass.validationNel)(Login[Validated])
}


