package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure

import scalaz.{\/, -\/, \/-}
final case class MovieYear[S <: ValidationStatus](y: Int) {
  private val MinYear: Int = 1870

  def validate[T >: S <: UnValidated]: ValidationFailure \/ MovieYear[Validated] =
    if(y < MinYear) -\/(NameShort) else \/-(MovieYear[Validated](y))
}

object MovieYear {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  implicit def toV(n: MovieYear[UnValidated]): ValidationFailure \/ MovieYear[Validated] = n.validate

  implicit def nameDecodeJson: DecodeJson[MovieYear[UnValidated]] =
    jdecode1L(MovieYear[UnValidated])("year")
}


case object InvalidYear extends ValidationFailure {
  def failMsg = s"Min year should be 1870"
}
