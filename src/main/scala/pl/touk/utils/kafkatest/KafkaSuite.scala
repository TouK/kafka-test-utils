package pl.touk.utils.kafkatest

import org.scalatest.{BeforeAndAfterAll, Suite}

trait KafkaSuite extends { self: Suite with BeforeAndAfterAll =>

  var kafkaServer: EmbeddedKafkaServer = _

  var kafkaClient: KafkaClient = _

  val kafkaBrokerConfig = Map.empty[String, String]

  override protected def beforeAll(): Unit = {
    val List(kafkaPort, zkPort) =
      AvailablePortFinder.findAvailablePorts(2)

    kafkaServer = EmbeddedKafkaServer(
      kafkaPort = kafkaPort,
      zkPort = zkPort,
      kafkaBrokerConfig = kafkaBrokerConfig)

    kafkaServer.run()

    kafkaClient = new KafkaClient(
      kafkaAddress = kafkaServer.kafkaAddress,
      zkAddress = kafkaServer.zkAddress)
  }

  override protected def afterAll(): Unit = {
    kafkaClient.shutdown()
    kafkaServer.shutdown()
  }
}