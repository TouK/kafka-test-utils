package pl.touk.utils.kafkatest

import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.Properties

import kafka.server.{KafkaConfig, KafkaServer}
import org.apache.kafka.common.utils.Time
import org.apache.zookeeper.server.{NIOServerCnxnFactory, ZooKeeperServer}
import pl.touk.utils.kafkatest.EmbeddedKafkaServer.{localhost, tempDir}

object EmbeddedKafkaServer {
  private val localhost = "127.0.0.1"

  private lazy val tempDir: File = Files.createTempDirectory("zkKafka").toFile

  def apply(zkPort: Int, kafkaPort: Int, kafkaBrokerConfig: Map[String, String]): EmbeddedKafkaServer =
    new EmbeddedKafkaServer(zkPort, kafkaPort, kafkaBrokerConfig)
}

class EmbeddedKafkaServer(zkPort: Int, kafkaPort: Int, kafkaBrokerConfig: Map[String, String]) {
  import KafkaConfig._

  private lazy val serverProperties = {
    val properties = new Properties()
    properties.setProperty(ZkConnectProp, zkAddress)
    properties.setProperty(BrokerIdProp, "0")
    properties.setProperty(HostNameProp, localhost)
    properties.setProperty(AdvertisedHostNameProp, localhost)
    properties.setProperty(NumPartitionsProp, "1")
    properties.setProperty(OffsetsTopicReplicationFactorProp, "1")
    properties.setProperty(LogCleanerDedupeBufferSizeProp, (2 * 1024 * 1024L).toString) //2MB should be enough for tests
    properties.setProperty(PortProp, s"$kafkaPort")
    properties.setProperty(LogDirProp, tempDir.getAbsolutePath)
    kafkaBrokerConfig.foreach { case (k, v) => properties.setProperty(k, v) }
    properties
  }

  private lazy val zooKeeperServer = new NIOServerCnxnFactory()

  private lazy val server = new KafkaServer(config = new KafkaConfig(serverProperties), time = Time.SYSTEM)

  val zkAddress: String = s"$localhost:$zkPort"

  val kafkaAddress: String = s"$localhost:$kafkaPort"

  def run(): Unit = {
    runZookeeper()
    server.startup()
  }

  def shutdown(): Unit = {
    server.shutdown()
    zooKeeperServer.shutdown()
  }

  private def runZookeeper(): Unit = {
    zooKeeperServer.configure(new InetSocketAddress(localhost, zkPort), 1024)
    zooKeeperServer.startup(new ZooKeeperServer(tempDir, tempDir, 100))
  }
}
