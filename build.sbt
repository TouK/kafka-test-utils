import Dependencies._
import sbt.Keys._
import sbt._

ThisBuild / scalaVersion := Versions.scalaV
ThisBuild / organization := "pl.touk"
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/TouK/kafka-test-utils"), "scm:git@github.com:TouK/kafka-test-utils.git")
)
ThisBuild / developers := List(
  Developer(
    id    = "TouK",
    name  = "TouK",
    email = "info@touk.pl",
    url   = url("http://touk.pl")
  )
)
val nexusHost = "oss.sonatype.org"
val nexusUrl = Option(System.getProperty("nexusUrl"))

ThisBuild / publishTo := nexusUrl.map { url =>
  (if (isSnapshot.value) "snapshots" else "releases") at s"https://$nexusHost/$url"
}
ThisBuild / publishMavenStyle := true

ThisBuild / credentials := Seq(Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  System.getProperty("nexusUser", "touk"),
  System.getProperty("nexusPassword"))
)

lazy val root = (project in file(".")).
  settings(
    name := "kafka-test-utils",
    libraryDependencies ++= Seq(
      kafka,
      kafkaClients,
      scalaTest))


