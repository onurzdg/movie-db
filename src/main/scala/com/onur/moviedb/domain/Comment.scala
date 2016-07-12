package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure

import scalaz.{\/-, -\/, \/}


final case class Comment[S <: ValidationStatus](c: String) {
  private val MinChars: Int = 10
  private val MaxChars: Int = 4000

  def validate[T >: S <: UnValidated]: ValidationFailure \/ Comment[Validated] =
    if (c.length < MinChars)
      -\/(NameShort)
    else if(c.length > MaxChars)
      -\/(NameLong)
    else
      \/-(Comment[Validated](c))
}


object Comment {
  import argonaut.Argonaut._
  import argonaut.DecodeJson

  type CommentValidation = ValidationFailure \/ Comment[Validated]

  implicit def toV(n: Comment[UnValidated]): CommentValidation = n.validate

  implicit def commentDecodeJson: DecodeJson[Comment[UnValidated]] =
    jdecode1L(Comment[UnValidated])("comment")

}

case object CommentTooShort extends ValidationFailure {
  def failMsg = "Comment is too short"
}
case object CommentTooLong extends ValidationFailure {
  def failMsg = "Comment is too long"
}
