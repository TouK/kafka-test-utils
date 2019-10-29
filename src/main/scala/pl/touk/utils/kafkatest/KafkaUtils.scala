package pl.touk.utils.kafkatest

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringSerializer}
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.time.{Millis, Seconds, Span}

import org.scalatest.concurrent.Eventually._

object KafkaUtils {

  def createRawKafkaProducer(kafkaAddress: String): KafkaProducer[Array[Byte], Array[Byte]] = {
    val props: Properties = createCommonProducerProps(kafkaAddress)
    props.put("key.serializer", classOf[ByteArraySerializer].getName)
    props.put("value.serializer", classOf[ByteArraySerializer].getName)
    new KafkaProducer(props)
  }

  def createKafkaProducer(kafkaAddress: String): KafkaProducer[String, String] = {
    val props: Properties = createCommonProducerProps(kafkaAddress)
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    new KafkaProducer(props)
  }

  private def createCommonProducerProps[K, T](kafkaAddress: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", kafkaAddress)
    props.put("batch.size", "100000")
    props.put("request.required.acks", "1")
    props
  }

  def createConsumerConnectorProperties(kafkaAddress: String, consumerTimeout: Long = 10000): Properties = {
    val props = new Properties()
    props.put("group.id", "testGroup")
    props.put("bootstrap.servers", kafkaAddress)
    props.put("auto.offset.reset", "earliest")
    props.put("consumer.timeout.ms", consumerTimeout.toString)
    props.put("key.deserializer", classOf[ByteArrayDeserializer])
    props.put("value.deserializer", classOf[ByteArrayDeserializer])
    props
  }

  case class KeyMessage[K, V](key: K, message: V)

  implicit class RichConsumerConnector(consumer: KafkaConsumer[Array[Byte], Array[Byte]]) {
    import scala.collection.JavaConverters._

    def consume(topic: String): Stream[KeyMessage[Array[Byte], Array[Byte]]] = {
      implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(10, Seconds), Span(100, Millis))
      val partitionsInfo = eventually {
        consumer.listTopics.asScala.getOrElse(topic, throw new IllegalStateException(s"Topic: $topic not exists"))
      }
      val partitions = partitionsInfo.asScala.map(no => new TopicPartition(topic, no.partition()))
      consumer.assign(partitions.asJava)

      Stream.continually(())
        .flatMap(_ => consumer.poll(1000).asScala.toStream)
        .map(record => KeyMessage(record.key(), record.value()))
    }
  }

}