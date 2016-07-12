package com.onur.moviedb.auth

import java.time.Instant

import com.onur.moviedb.db.DB
import com.onur.moviedb.domain.{IpAddress, SessionToken, UserId}
import scalaz.Maybe
import scalaz.concurrent.Task
import doobie.imports._

class SessionService(dao: SessionDAO) {
  import com.onur.moviedb.concurrency.ExecutionContext.dbContext
  val SessionDurationSecs = 3600

  def createSession(userId: UserId, ipAddress: Maybe[IpAddress]): Task[Session] = {
    val sess = Session(SessionToken.generateToken,
      Instant.now,
      Instant.now.plusSeconds(SessionDurationSecs),
      ipAddress,
      userId
    )
    Task.fork(dao.createSession(sess).transact(DB.xa)).map(_ => sess)
  }

  def getSessionByToken(t: SessionToken): Task[Maybe[Session]] =
    Task.fork(dao.getSessionByToken(t).transact(DB.xa))

  def deleteSessions(uid: UserId): Task[Boolean] = Task.fork(dao.deleteSessions(uid).transact(DB.xa))

  def deleteSession(token: SessionToken): Task[Boolean] = Task.fork(dao.deleteSession(token).transact(DB.xa))
}
