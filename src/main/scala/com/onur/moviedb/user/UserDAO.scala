package com.onur.moviedb.user

import java.time.Instant

import com.onur.moviedb.auth.EncryptedPassword
import com.onur.moviedb.db.Mapping._
import com.onur.moviedb.domain._
import doobie.imports._

import scalaz.Maybe

trait UserDAO {
  def createUser(u: UserCreation[Validated], enc: EncryptedPassword, accState: AccountState): ConnectionIO[User] =
    sql"""insert into user (name, email, password, privilege_id, account_state_id, created_at)"
       values (${u.name}, ${u.email}, ${u.password}, ${u.privilege}, ${accState},
      now())""".update.withUniqueGeneratedKeys[(Long, Instant)]("id", "created_at").
      map{case (id: Long, creationDate: Instant) =>
        User(UserId(id), u.name, u.email, enc, u.privilege,
          accState, creationDate, Maybe.empty, Maybe.empty)}

  def deleteUser(ui: UserId): ConnectionIO[Boolean] =
    sql"delete from user where id = ${ui.id})".update.run.map(_ > 0)

  def updatePassword(ui: UserId, enc: EncryptedPassword): ConnectionIO[Boolean] =
    sql"update user set password = ${enc.pass} where id = ${ui.id})".update.run.map(_ > 0)

  def getUsers: ConnectionIO [List[User]] =
    sql"select * from user".query[User].list

  def getUserByEmail(email: Email[Validated]): ConnectionIO[Maybe[User]] =
    sql"select * from user where email = ${email.email}".query[User].option.map(Maybe.fromOption(_))

  def getUserById(uid: UserId): ConnectionIO[Maybe[User]] =
    sql"select * from user where id = ${uid.id}".query[User].option.map(Maybe.fromOption(_))

}
