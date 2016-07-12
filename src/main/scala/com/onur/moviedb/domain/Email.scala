package com.onur.moviedb.domain


import com.onur.moviedb.failure.ValidationFailure
import org.apache.commons.validator.routines.EmailValidator

import scalaz._

final case class Email[S <: ValidationStatus](email: String) {
  def validate[T >: S <: UnValidated]: ValidationFailure \/ Email[Validated] =
    EmailValidator.getInstance.isValid(email) match {
      case true => \/-(Email[Validated](email))
      case false => -\/(InvalidEmail)
    }
}

object Email {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  type EmailValidation = ValidationFailure \/ Email[Validated]

  implicit def toV(e: Email[UnValidated]): EmailValidation = e.validate

  implicit def emailUDecodeJson: DecodeJson[Email[UnValidated]] =
    jdecode1L(Email[UnValidated])("email")

}

case object InvalidEmail extends ValidationFailure {
  override def failMsg: String = "Invalid email address"
}