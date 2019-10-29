import sbt._
import Versions._

object Dependencies {
  val kafka = "org.apache.kafka" %% "kafka" % kafkaV
  val kafkaClients = "org.apache.kafka" % "kafka-clients" % kafkaV
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV
}