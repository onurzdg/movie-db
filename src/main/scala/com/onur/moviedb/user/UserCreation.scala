package com.onur.moviedb.user

import argonaut.DecodeJson
import com.onur.moviedb.auth.ClearTextPassword
import com.onur.moviedb.domain._
import com.onur.moviedb.failure.ValidationFailure

import scalaz.ValidationNel
import scalaz.syntax.applicative._
import argonaut.Argonaut._

final case class UserCreation[S <: ValidationStatus](
  name: Name[S],
  email: Email[S],
  password: ClearTextPassword[S],
  privilegeStr: String) {
    def privilege[T >: S <: Validated] = UserPrivilege.unsafeFromString(privilegeStr)
}

object UserCreation {
  type UserCreationValidation = ValidationNel[ValidationFailure, UserCreation[Validated]]

  implicit def personDecodeJson: DecodeJson[UserCreationValidation] =
    DecodeJson(c =>
      (
        c.as[Name[UnValidated]] |@|
          c.as[Email[UnValidated]] |@|
          c.as[ClearTextPassword[UnValidated]] |@|
          (c --\ "privilege").as[String]
        ) ((n, e, p, priv) => validate(UserCreation[UnValidated](n, e, p, priv)))
    )


  private def validate(u: UserCreation[UnValidated]): UserCreationValidation =
    (u.name.validationNel |@| u.email.validationNel |@| u.password.validationNel |@|
      UserPrivilege.validation(u.privilegeStr)) (
      (a,b,c,d) => UserCreation[Validated](a,b,c,d.title)
    )
}
