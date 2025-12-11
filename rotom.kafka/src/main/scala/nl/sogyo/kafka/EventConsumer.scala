package nl.sogyo.kafka

import com.typesafe.scalalogging.Logger
import nl.sogyo.kafka.Topics
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import io.circe.*
import io.circe.parser.*
import io.circe.generic.auto.*

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import nl.sogyo.persistence.*

class EventConsumer {
  
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

  private def processRecord(record: ConsumerRecord[String, String], databaseWriter: DatabaseWriter) = 
    record.topic() match
      case Topics.newLoans => 
        val loanEither = decode[Loan](record.value())
        loanEither match
          case Left(error) => 
            logger.error("Unable to decode new_loans record to Loan instance.")
          case Right(loan) =>
            databaseWriter.insertNewLoans(Seq(loan))
      case _ =>
        logger.error(s"""Received record of unimplemented topic: 
          Topic:\t${record.topic()}
          Partition:\t${record.partition()}
          Offset:\t${record.offset()}
          Key:\t${record.key()}
          Value:\t${record.value()} 
          """)
      
  def run(databaseWriter: DatabaseWriter): Unit =
    consumer.subscribe(Topics.topics)
    try
      while true do
          val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))
          for record <- records.asScala do
            processRecord(record, databaseWriter)
    finally
      consumer.close()
}
