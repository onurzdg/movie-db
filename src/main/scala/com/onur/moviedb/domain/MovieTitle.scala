package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure

import scalaz.{-\/, \/, \/-}

final case class MovieTitle[S <: ValidationStatus](t: String) {
  private val MinChars: Int = 2
  private val MaxChars: Int = 150

  def validate[T >: S <: UnValidated]: ValidationFailure \/ MovieTitle[Validated] =
    if (t.length < MinChars)
      -\/(NameShort)
    else if(t.length > MaxChars)
      -\/(NameLong)
    else
      \/-(MovieTitle[Validated](t))
}

object MovieTitle {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  implicit def toV(n: MovieTitle[UnValidated]): ValidationFailure \/ MovieTitle[Validated] = n.validate

  implicit def nameDecodeJson: DecodeJson[MovieTitle[UnValidated]] =
    jdecode1L(MovieTitle[UnValidated])("title")

}

case object TitleShort extends ValidationFailure {
  def failMsg = "Title is too short"
}
case object TitleLong extends ValidationFailure {
  def failMsg = "Title is too long"
}