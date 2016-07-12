package com.onur.moviedb.domain

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import com.onur.moviedb.failure.ValidationFailure
import doobie.util.meta.Meta

import scalaz.Maybe.Just
import scalaz._

sealed trait UserPrivilege extends Product with Serializable {
  def value: Int
  def title: String
}

case object RegularUser extends UserPrivilege {
  override def title = "regular-user"
  override def value = 0
}

case object Admin extends UserPrivilege {
  override def title = "admin"
  override def value = 1
}


object UserPrivilege {
  implicit val userPriv: Meta[UserPrivilege] = Meta[Int].xmap(unsafeFromInt, _.value)

  def fromInt (priv: Int): Maybe[UserPrivilege] =
    priv match {
      case 0 => Just(RegularUser)
      case 1 => Just(Admin)
      case _ => Maybe.empty
    }

  def fromString (priv: String): Maybe[UserPrivilege] = priv.toLowerCase match {
      case "regular-user" => Just(RegularUser)
      case "admin" => Just(Admin)
      case _ => Maybe.empty
    }

  def validation(priv: String): ValidationNel[ValidationFailure, UserPrivilege] =
    fromString(priv).toRight(InvalidUserPrivilege(s"invalid priv $priv")).validationNel

  def unsafeFromInt(priv: Int): UserPrivilege =
    fromInt(priv).getOrElse(throw new AssertionError(s"Invalid privilige: $priv"))

  def unsafeFromString(priv: String): UserPrivilege =
    fromString(priv).getOrElse(throw new AssertionError(s"Invalid privilege: $priv"))

  implicit def userPrivEncodeJson: EncodeJson[UserPrivilege] =
    jencode2L((u: UserPrivilege) => (u.title, u.value))("title", "value")

  implicit def userPrivDecodeJson: DecodeJson[Maybe[UserPrivilege]] =
    jdecode1L(UserPrivilege.fromString)("privilege")
}

final case class InvalidUserPrivilege(p: String) extends ValidationFailure {
  def failMsg = s"Invalid user privilege id $p"
}
