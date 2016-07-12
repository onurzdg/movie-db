package com.onur.moviedb.auth

import java.time.Instant

import argonaut.Argonaut._
import argonaut.EncodeJson
import com.onur.moviedb.domain._

import scalaz.Maybe

final case class Session(
  token: SessionToken,
  startAt: Instant,
  endAt: Instant,
  ipAddress: Maybe[IpAddress],
  uid: UserId)

object Session {
  implicit def sessEncodeJson: EncodeJson[Session] =
    jencode5L((s: Session) =>
      ( s.token.id,
        s.startAt.toString,
        s.endAt.toString,
        s.ipAddress.map(_.ip),
        s.uid.id)
    )("id", "starAt", "endAt", "ipAddress", "userId")
}
