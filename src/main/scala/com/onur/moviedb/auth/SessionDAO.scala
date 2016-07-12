package com.onur.moviedb.auth

import com.onur.moviedb.domain._
import doobie.imports._
import com.onur.moviedb.db.Mapping._

import scalaz.Maybe

trait SessionDAO {

  def createSession(s: Session): ConnectionIO[Int] = {
    val sql = "insert into session(id, start_at, end_at, ip_address, user_id) values(?,?,?,?,?)"
    Update[Session](sql).run(s)
  }

  def getSessionByToken(t: SessionToken): ConnectionIO[Maybe[Session]] =
    sql"select * from session where id = ${t.id}".query[Session].option.map(Maybe.fromOption)
  
  def deleteSessions(uid: UserId): ConnectionIO[Boolean] =
    sql"delete from session where id = ${uid.id})".update.run.map(_ > 0)

  def deleteSession(token: SessionToken): ConnectionIO[Boolean] =
    sql"delete from session where id = ${token.id})".update.run.map(_ > 0)
}
