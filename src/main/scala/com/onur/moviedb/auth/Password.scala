package com.onur.moviedb.auth

import argonaut.Argonaut._
import argonaut.DecodeJson
import com.onur.moviedb.domain.{UnValidated, Validated, ValidationStatus}
import com.onur.moviedb.failure.ValidationFailure

import scalaz.{-\/, \/, \/-}

sealed trait Password extends Product with Serializable
final case class EncryptedPassword(pass: String) extends Password
final case class ClearTextPassword[S <: ValidationStatus](pass: String) extends Password {
  private val MinChars: Int = 8
  private val MaxChars: Int = 40

  def validate[T >: S <: UnValidated]: ValidationFailure \/ ClearTextPassword[Validated] =
    if (pass.length < MinChars)
      -\/(PasswordShort)
    else if(pass.length > MaxChars)
      -\/(PasswordLong)
    else
      \/-(ClearTextPassword[Validated](pass))
}

object ClearTextPassword {
  implicit def toV(c: ClearTextPassword[UnValidated]): ValidationFailure \/ ClearTextPassword[Validated] =
    c.validate

  implicit def personDecodeJson: DecodeJson[ClearTextPassword[UnValidated]] =
    jdecode1L(ClearTextPassword[UnValidated])("password")
}

case object PasswordShort extends ValidationFailure {
  def failMsg = "Password is too short"
}

case object PasswordLong extends ValidationFailure {
  def failMsg = "Password is too long"
}
