name := "es-case"

version := "0.0.1"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "org.json4s"   %% "json4s-native" % "3.0.0",
  "org.json4s"   %% "json4s-jackson" % "3.0.0",
  "org.slf4j" % "slf4j-api" % "1.6.5",
  "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
  "org.eclipse.jetty" % "jetty-client" % "8.1.7.v20120910"
)