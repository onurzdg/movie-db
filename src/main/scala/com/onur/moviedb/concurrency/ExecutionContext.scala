package com.onur.moviedb.concurrency

import java.util.concurrent.{ExecutorService, Executors}

import com.onur.moviedb.conf.SiteConfig


object ExecutionContext {
  // Thread count here should be equal to the db connection count in datasource
  implicit val dbContext: ExecutorService = Executors.newFixedThreadPool(SiteConfig.dbConf.connectionCount)
}
