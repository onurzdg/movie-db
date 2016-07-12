package com.onur.moviedb.conf

import com.typesafe.config.{Config, ConfigFactory}

object SiteConfig {

  private val conf: Config = ConfigFactory.load


  final case class JettyConf(
    maxThreads: Int,
    minThreads: Int,
    idleTimeout: Int,
    acceptQueueSize: Int,
    securePort: Int,
    unSecurePort: Int,
    poolName: String
  )

  def jettyConf: JettyConf =
    JettyConf(
      conf.getInt("jetty.maxThreads"),
      conf.getInt("jetty.minThreads"),
      conf.getInt("jetty.idleTimeout"),
      conf.getInt("jetty.acceptQueueSize"),
      conf.getInt("jetty.securePort"),
      conf.getInt("jetty.unSecurePort"),
      conf.getString("jetty.poolName")
  )

  final case class DbConf(
    host: String,
    port: Int,
    name: String,
    username: String,
    password: String,
    connectionCount: Int,
    urlOptions: String,
    connectionTimeout: Long,
    idleTimeout: Long,
    maxLifetime: Long,
    validationTimeout: Long,
    poolName: String
  )

  def dbConf: DbConf =
    DbConf(
      conf.getString("db.host"),
      conf.getInt("db.port"),
      conf.getString("db.name"),
      conf.getString("db.user"),
      conf.getString("db.password"),
      conf.getInt("db.connectionCount"),
      conf.getString("db.urlOptions"),
      conf.getLong("db.connectionTimeout"),
      conf.getLong("db.idleTimeout"),
      conf.getLong("db.maxLifetime"),
      conf.getLong("db.validationTimeout"),
      conf.getString("db.poolName")
    )
}
