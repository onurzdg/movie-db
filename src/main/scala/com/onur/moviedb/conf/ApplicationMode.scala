package com.onur.moviedb.conf

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import com.onur.moviedb.failure.ValidationFailure

import scalaz.Maybe.Just
import scalaz.Maybe

sealed trait ApplicationMode extends Product with Serializable {
  def value: Int
  def title: String
}

case object Development extends ApplicationMode {
  override def title = "development"
  override def value = 0
}

case object Server extends ApplicationMode {
  override def title = "server"
  override def value = 1
}

case object Container extends ApplicationMode {
  override def title = "container"
  override def value = 2
}

object ApplicationMode {
  def fromInt (mode: Int): Maybe[ApplicationMode] =
    mode match {
      case 0 => Just(Development)
      case 1 => Just(Server)
      case 2 => Just(Container)
      case _ => Maybe.empty
    }

  def fromString (mode: String): Maybe[ApplicationMode] =
    mode.toLowerCase match {
      case "development" => Just(Development)
      case "server" => Just(Server)
      case "container" => Just(Container)
      case _ => Maybe.empty
    }

  implicit def applicationModeEncodeJson: EncodeJson[ApplicationMode] =
    jencode2L((a: ApplicationMode) => (a.title, a.value))("title", "value")

  implicit def userPrivDecodeJson: DecodeJson[Maybe[ApplicationMode]] =
    jdecode1L(fromString)("applicationMode")
}

final case class InvalidApplicationMode(m: String) extends ValidationFailure {
  def failMsg = s"Invalid application mode $m"
}

