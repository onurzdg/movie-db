package com.onur.moviedb.domain

import java.math.BigInteger
import java.security.SecureRandom

final case class SessionToken(id: String)

object SessionToken {
  private val random: SecureRandom = new SecureRandom
  def generateToken: SessionToken = SessionToken(new BigInteger(130, random).toString(32))
}
