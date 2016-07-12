package com.onur.moviedb.db

import java.sql.Timestamp
import java.time.Instant

import doobie.util.meta.Meta

object Mapping {
  implicit val InstantMeta: Meta[Instant] =
    Meta[Timestamp].nxmap(_.toInstant, Timestamp.from)
}
