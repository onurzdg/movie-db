package com.onur.moviedb.conf

import org.apache.logging.log4j.Level

import scalaz.Maybe.Just
import scalaz.Maybe


object Environment {
  private val ApplicationModeEnv = "APPLICATION_MODE"
  private val SyslogHostEnv = "SYSLOG_HOST"
  private val SyslogPort = "SYSLOG_PORT"

  def getApplicationMode: Maybe[ApplicationMode] = sys.env.get(ApplicationModeEnv) match {
    case Some(mode) => ApplicationMode.fromString(mode)
    case _ => Just(Development)
  }

  def getSyslogHost: Maybe[String] = Maybe.fromOption(sys.env.get(SyslogHostEnv))

  def getSyslogPort: Maybe[String] = Maybe.fromOption(sys.env.get(SyslogPort))

  def isInContainer: Boolean = getApplicationMode.exists(_ == Container)

  def isInDevelopment: Boolean = getApplicationMode.exists(_ == Development)

  def isInServer: Boolean = getApplicationMode.exists(_ == Server)

  def writeFolderPath: String = if (isInContainer) "/SCRATCH_VOLUME/moviedb/" else ""

  def sysOutLogLevel: String = if (isInContainer && isInServer) Level.OFF.toString else Level.INFO.toString
}
