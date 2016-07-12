name := "movie-db"

version := "1.0"

scalaVersion := "2.11.8"


scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Xfuture",
  "-target:jvm-1.8"
)

cancelable in Global := true

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.sonatypeRepo("snapshots")

wartremoverErrors in (Compile, compile) ++=
  Warts.allBut(Wart.Any, Wart.Nothing, Wart.NonUnitStatements, Wart.AsInstanceOf, Wart.Throw)

lazy val doobieVersion = "0.2.4"
lazy val http4sVersion = "0.13.2"
lazy val rhoVersion = "0.10.1"
lazy val jettyVersion = "9.3.8.v20160314"
lazy val metricsVersion = "3.1.2"
lazy val log4jVersion = "2.5"

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "2.4.4",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
  "org.tpolecat" %% "doobie-core"           % doobieVersion,
  "org.tpolecat" %% "doobie-contrib-specs2" % doobieVersion,
  "org.tpolecat" %% "doobie-contrib-postgresql" % doobieVersion,
  "org.http4s"   %% "http4s-dsl"            % http4sVersion,
  "org.http4s"   %% "http4s-server"         % http4sVersion,
  "org.http4s"   %% "http4s-jetty"          % http4sVersion,
  "org.http4s"   %% "http4s-argonaut61"     % http4sVersion,
  "org.http4s"   %% "rho-core"              % rhoVersion,
  "io.argonaut"  %% "argonaut"              % "6.1",
  "org.eclipse.jetty"  % "jetty-server"     % jettyVersion,
  "org.eclipse.jetty"  % "jetty-servlet"    % jettyVersion,
  "io.dropwizard.metrics" % "metrics-core" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-servlet" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-servlets" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-jetty9" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-jvm" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-healthchecks" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-json" % metricsVersion,
  "io.dropwizard.metrics" % "metrics-servlet" % metricsVersion,
  "org.apache.logging.log4j" % "log4j-api" %  log4jVersion,
  "org.apache.logging.log4j" % "log4j-core" %  log4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" %  log4jVersion,
  "org.slf4j" % "slf4j-api" % "1.7.19",
  "com.typesafe" % "config" % "1.3.0",
  "org.log4s" %% "log4s" % "1.3.0",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "commons-validator" % "commons-validator" % "1.5.1"
)


flywayUrl := "jdbc:postgresql://localhost:5432/moviedb"

flywayUser := "app"

flywayPassword := "app"