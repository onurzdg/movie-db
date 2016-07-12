package com.onur.moviedb.user

import com.onur.moviedb.auth.PasswordEncryption
import com.onur.moviedb.db.DB
import com.onur.moviedb.domain._
import doobie.contrib.postgresql.sqlstate.class23


import scalaz.{Maybe, \/}
import com.onur.moviedb.failure.OperationFailure
import doobie.imports._

import scalaz.concurrent.Task

class UserService (dao: UserDAO) {
  import com.onur.moviedb.concurrency.ExecutionContext.dbContext

  def createUser(u: UserCreation[Validated]): Task[UserCreationFailure \/ User] = {
    val hash = PasswordEncryption.createHash(u.password)
    Task.fork(dao.createUser(u, hash, Active).
      attemptSomeSqlState({case class23.UNIQUE_VIOLATION => UserAlreadyExists}).
      transact(DB.xa))
  }

  def getUsers: Task[List[User]] =
    Task.fork(dao.getUsers.transact(DB.xa))

  def deleteUser(ui: UserId): Task[Boolean] =
    Task.fork(dao.deleteUser(ui).transact(DB.xa))

  def getUserById(id: UserId): Task[Maybe[User]] =
    Task.fork(dao.getUserById(id).transact(DB.xa))

  def getUserByEmail(email: Email[Validated]): Task[Maybe[User]] =
    Task.fork(dao.getUserByEmail(email).transact(DB.xa))

  sealed trait UserCreationFailure extends OperationFailure
  case object UserAlreadyExists extends UserCreationFailure {
    override def failMsg: String = "Email address is already registered to another user"
  }
}
