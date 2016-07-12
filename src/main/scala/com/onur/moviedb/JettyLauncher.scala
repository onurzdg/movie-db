package com.onur.moviedb

import org.apache.logging.log4j.{Level, ThreadContext}
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.{Connector, _}
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.servlet.{ErrorPageErrorHandler, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.{Logger, LoggerFactory}

object JettyLauncher {

  def main(args: Array[String]): Unit = {
    configureLogger()
    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    startJetty(logger)
  }

  @throws[Exception]
  private def startJetty(logger: Logger): Unit = {
    logger.info("---- Starting server -----")
    val qtp: QueuedThreadPool = new QueuedThreadPool
    qtp.setIdleTimeout(300000)
    qtp.setMinThreads(50)
    qtp.setMaxThreads(128)
    val server: Server = new Server(qtp)
    val httpsConfig: HttpConfiguration = new HttpConfiguration
    httpsConfig.setSendServerVersion(false)
    val httpConfig: HttpConfiguration = new HttpConfiguration
    httpConfig.setSendServerVersion(false)
    val httpConnector: ServerConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig))
    httpConnector.setName("unsecured")
    httpConnector.setPort(8080)
    httpConnector.setReuseAddress(true)
    val contextHandler: ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    contextHandler.addServlet(new ServletHolder("staticHolder", ServBuilder.staticAPI), "/*")
    contextHandler.addServlet(new ServletHolder("privateHolder", ServBuilder.privateAPI), "/api/*")
    contextHandler.addServlet(new ServletHolder("publicHolder", ServBuilder.publicAPI), "/api/public/*")
    val gzipHandler: GzipHandler = new GzipHandler
    gzipHandler.setIncludedMimeTypes("text/html", "text/plain", "text/xml",
      "text/javascript", "text/css", "application/javascript", "image/svg+xml")
    gzipHandler.setMinGzipSize(256)
    gzipHandler.setIncludedPaths("/*")
    contextHandler.setGzipHandler(gzipHandler)
    val errorHandler: ErrorPageErrorHandler = new ErrorPageErrorHandler
    errorHandler.addErrorPage(404, "/error.html")
    contextHandler.setErrorHandler(errorHandler)
    val requestLog: Slf4jRequestLog = new Slf4jRequestLog
    requestLog.setExtended(false)
    requestLog.setLogLatency(true)
    requestLog.setLogCookies(true)
    requestLog.setExtended(true)
    requestLog.setLogTimeZone("GMT")
    server.setRequestLog(requestLog)
    val hlist: HandlerList = new HandlerList
    hlist.setHandlers(Array(contextHandler))
    server.setHandler(hlist)
    server.setConnectors(Array[Connector](httpConnector))
    server.start()
    logger.info("---- Jetty server started -----")
  }

  private def configureLogger(): Unit = {
    ThreadContext.put("STD_OUT_LOG_LEVEL", Level.INFO.toString)
    ThreadContext.put("BASE_VOL", "")
    try {
      Thread.sleep(1000)
    }
    catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
  }
}
