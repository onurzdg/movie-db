package com.onur.moviedb.domain


import com.onur.moviedb.failure.ValidationFailure

import scalaz.{-\/, \/, \/-}

/**
 *
 * @param name
 * @tparam S
 */
final case class Name[S <: ValidationStatus](name: String) {
  private val MinChars: Int = 2
  private val MaxChars: Int = 30

  def validate[T >: S <: UnValidated]: ValidationFailure \/ Name[Validated] =
    if (name.length < MinChars)
      -\/(NameShort)
    else if(name.length > MaxChars)
      -\/(NameLong)
    else
      \/-(Name[Validated](name))
}

object Name {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  implicit def toV(n: Name[UnValidated]): ValidationFailure \/ Name[Validated] = n.validate

  implicit def nameDecodeJson: DecodeJson[Name[UnValidated]] =
    jdecode1L(Name[UnValidated])("name")
}

case object NameShort extends ValidationFailure {
  def failMsg = "Name is too short"
}
case object NameLong extends ValidationFailure {
  def failMsg = "Name is too long"
}
