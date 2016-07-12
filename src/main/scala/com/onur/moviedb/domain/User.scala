package com.onur.moviedb.domain

import java.time.Instant

import argonaut.Argonaut._
import argonaut.EncodeJson
import com.onur.moviedb.auth.EncryptedPassword

import scalaz.Maybe

final case class User(
  id: UserId,
  name: Name[Validated],
  email: Email[Validated],
  password: EncryptedPassword,
  privilege: UserPrivilege,
  accountState: AccountState,
  createdAt: Instant,
  updatedAt: Maybe[Instant],
  suspendedAt: Maybe[Instant]
)


object User {
  implicit def userEncodeJson: EncodeJson[User] =
    jencode8L((u: User) =>
      ( u.id.id,
        u.name.name,
        u.email.email,
        u.privilege.title,
        u.accountState.title,
        u.createdAt.toString,
        u.updatedAt.map(_.toString),
        u.updatedAt.map(_.toString))
    )("id", "name", "email", "privilege", "state", "createdAt", "updatedAt", "suspendedAt")
}
