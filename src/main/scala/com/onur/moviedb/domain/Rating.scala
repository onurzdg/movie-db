package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure

import scalaz.{-\/, \/, \/-}

final case class Rating[S <: ValidationStatus](r: Float) {
  private val MinStar: Int = 1
  private val MaxStar: Int = 10

  def validate[T >: S <: UnValidated]: ValidationFailure \/ Rating[Validated] =
    if (r < MinStar)
      -\/(RatingLow)
    else if(r > MaxStar)
      -\/(RatingHigh)
    else
      \/-(Rating[Validated](r))
}

object Rating {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  implicit def toV(n: Rating[UnValidated]): ValidationFailure \/ Rating[Validated] = n.validate

  implicit def nameDecodeJson: DecodeJson[Rating[UnValidated]] =
    jdecode1L(Rating[UnValidated])("rating")
}

case object RatingLow extends ValidationFailure {
  def failMsg = "Rating is too low: min rating value is 1"
}
case object RatingHigh extends ValidationFailure {
  def failMsg = "Rating is too high: max rating value is 10"
}