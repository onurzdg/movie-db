package com.onur.moviedb.domain

import com.onur.moviedb.failure.ValidationFailure
import doobie.util.meta.Meta

import scalaz.Maybe.Just
import scalaz.Maybe

sealed trait AccountState extends Product with Serializable {
  def value: Int
  def title: String
}

case object Active extends AccountState {
  override def title = "active"
  override def value = 0
}

case object Suspended extends AccountState {
  override def title = "suspended"
  override def value = 1
}

object AccountState {
  import argonaut.Argonaut._
  import argonaut.{DecodeJson, EncodeJson}

  implicit def userPriv: Meta[AccountState] = Meta[Int].xmap(unsafeFromInt, _.value)

  def fromInt (state: Int): Maybe[AccountState] = state match {
    case 0 => Just(Active)
    case 1 => Just(Suspended)
    case _ => Maybe.empty
  }

  def unsafeFromInt(state: Int): AccountState =
    fromInt(state).getOrElse(throw new RuntimeException(s"Invalid account state: $state"))

  def unsafeFromString(state: String): AccountState =
    fromString(state).getOrElse(throw new RuntimeException(s"Invalid account state: $state"))

  def fromString (state: String): Maybe[AccountState] = state.toLowerCase match {
    case "active" => Just(Active)
    case "suspended" => Just(Suspended)
    case _ => Maybe.empty
  }

  implicit def userPrivEncodeJson: EncodeJson[UserPrivilege] =
    jencode2L((u: UserPrivilege) => (u.title, u.value))("title", "value")

  implicit def userPrivDecodeJson: DecodeJson[Maybe[AccountState]] =
    jdecode1L(fromString)("state")
}

final case class InvalidAccountState(s: String) extends ValidationFailure {
  def failMsg = s"Invalid user account state $s"
}
