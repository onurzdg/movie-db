package com.onur.moviedb.user


trait UserModule {
  lazy val dao = new UserDAO {}
  lazy val userService = new UserService(dao)
}

object UserModule extends UserModule
