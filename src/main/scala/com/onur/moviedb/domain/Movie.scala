package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure

import scalaz._
import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}

import scalaz.ValidationNel
import scalaz.syntax.applicative._

final case class Movie[S <: ValidationStatus] (
  id: Maybe[MovieId],
  title: MovieTitle[S],
  year: MovieYear[S],
  imdbRating: Rating[S]
)

object Movie {
  type MovieValidation = ValidationNel[ValidationFailure, Movie[Validated]]

  implicit def encodeMovie: EncodeJson[Movie[Validated]] =
    jencode4L((m: Movie[Validated]) =>
      (m.id.map(_.id), m.title.t, m.year.y,
       m.imdbRating.r)) ("movieId", "title", "year", "imdbRating")

  implicit def decodeMovie: DecodeJson[MovieValidation] = DecodeJson(c =>
    (c.as[Maybe[MovieId]] |@|
      c.as[MovieTitle[UnValidated]] |@|
      c.as[MovieYear[UnValidated]] |@|
      c.as[Rating[UnValidated]]
      ) ((id, t, y, r) => validate(Movie[UnValidated](id, t, y, r)))
  )

  private def validate(m: Movie[UnValidated]): MovieValidation =
    ( m.title.validationNel |@|
      m.year.validationNel |@|
      m.imdbRating.validationNel)((t,y,r) => Movie[Validated](m.id,t,y,r) )

}


final case class OwnedMovie(
  title: MovieTitle[Validated],
  movieYear: MovieYear[Validated],
  imdbRating: Rating[Validated],
  digitalCopy: Maybe[DigitalCopy],
  physicalCopy: Boolean,
  shelfNo: Maybe[Int],
  personalRating: Maybe[Rating[Validated]],
  comment: Maybe[Comment[Validated]])

object OwnedMovie {
  implicit def applicationModeEncodeJson: EncodeJson[OwnedMovie] =
    jencode8L((m: OwnedMovie) => (m.title.t, m.movieYear.y,  m.imdbRating.r, m.digitalCopy, m.physicalCopy,
      m.shelfNo, m.personalRating.map(_.r), m.comment.map(_.c) ))("title", "year",  "imdbRating",
      "digitalCopy", "physicalCopy", "shelfNo", "personalRating", "comment")
}
