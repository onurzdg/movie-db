package com.onur.moviedb.domain

import scalaz.Maybe
import scalaz.Maybe.Just

sealed trait DigitalCopy extends Product with Serializable {
  def value: Int
  def title: String
}

case object Netflix extends DigitalCopy {
  override def title = "youtube"
  override def value = 0
}

case object Amazon extends DigitalCopy {
  override def title = "amazon"
  override def value = 1
}

object DigitalCopy {
  import argonaut.Argonaut._
  import argonaut.{DecodeJson, EncodeJson}

  def fromInt (mode: Int): Maybe[DigitalCopy] =
    mode match {
      case 0 => Just(Netflix)
      case 1 => Just(Amazon)
      case _ => Maybe.empty
    }

  def fromString (mode: String): Maybe[DigitalCopy] =
    mode.toLowerCase match {
      case "youtube" => Just(Netflix)
      case "amazon" => Just(Amazon)
      case _ => Maybe.empty
    }

  implicit def applicationModeEncodeJson: EncodeJson[DigitalCopy] =
    jencode2L((a: DigitalCopy) => (a.title, a.value))("title", "value")

  implicit def userPrivDecodeJson: DecodeJson[Maybe[DigitalCopy]] =
    jdecode1L(fromString)("digitalCopy")
}