package nl.sogyo.projector

import com.typesafe.scalalogging.Logger
import nl.sogyo.kafka.Topics
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.*


object Main:

  val logger = Logger(getClass.getName)

  private def setupProperties(): Properties =
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props

  val props = setupProperties()
  val consumer = new KafkaConsumer[String, String](props)

  @main def app(): Unit = 
    consumer.subscribe(Topics.topics)
    while true do
      try 
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))
        for record <- records.asScala do
          logger.info(s"""
          Topic:\t${record.topic()}
          Partition:\t${record.partition()}
          Offset:\t${record.offset()}
          Key:\t${record.key()}
          Value:\t${record.value()} 
          """)
        finally
          consumer.close()
