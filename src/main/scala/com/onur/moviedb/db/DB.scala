package com.onur.moviedb.db

import javax.sql.DataSource

import com.onur.moviedb.conf.SiteConfig
import com.onur.moviedb.metric.Metrics
import com.zaxxer.hikari.HikariDataSource
import doobie.imports._

import scalaz.concurrent.Task

object DB {
  lazy val dataSource: DataSource = {
    val ds = new HikariDataSource
    val dbConf = SiteConfig.dbConf
    val urlBuilder = new StringBuilder("jdbc:postgresql://")
    urlBuilder.append(dbConf.host)
      .append(":")
      .append(dbConf.port).append("/")
      .append(dbConf.name)
      .append(dbConf.urlOptions)
      .append("&prepStmtCacheSize=250")
      .append("&prepStmtCacheSqlLimit=2048")

    ds.setJdbcUrl(urlBuilder.toString)
    ds.setUsername(dbConf.username)
    ds.setPassword(dbConf.password)
    ds.setConnectionTimeout(dbConf.connectionTimeout)
    ds.setIdleTimeout(dbConf.idleTimeout)
    ds.setMaximumPoolSize(dbConf.connectionCount)
    ds.setMaxLifetime(dbConf.maxLifetime)
    ds.setPoolName(dbConf.poolName)
    ds.setRegisterMbeans(true)
    ds.setValidationTimeout(dbConf.validationTimeout)
    ds.setMetricRegistry(Metrics.metricRegistry)
    ds.setHealthCheckRegistry(Metrics.healthCheckRegistry)
    ds.addHealthCheckProperty("expected99thPercentileMs", "100")
    sys.addShutdownHook(ds.close())
    ds
  }

  lazy val xa = DataSourceTransactor[Task].apply(DB.dataSource)
}
