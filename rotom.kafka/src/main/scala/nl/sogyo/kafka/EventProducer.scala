package nl.sogyo.kafka

import io.circe.generic.auto.*
import io.circe.syntax.*
import nl.sogyo.persistence.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthorizationException
import org.apache.kafka.common.errors.ProducerFencedException

import java.util.Properties
import org.apache.kafka.clients.producer.ProducerConfig
import cats.effect.*


class EventProducer {
  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "test-transactional-id-1")
  props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  // props.put("acks","all")
  
  val producer = new KafkaProducer[String, String](props)
  val topic = "new_loans"

  var key = 0

  def open(): IO[Unit] =IO(producer.initTransactions())
  
  def close(): IO[Unit] = IO(producer.close())

  def sendLoanRequestEvent(loan: Loan): Unit = 
    println("Info: Converting loan to json string")
    val jsonString: String = loan.asJson.noSpaces
    println("Info: Posting loan request event")
    try {
      producer.beginTransaction()
      producer.send(new ProducerRecord[String, String](topic, key.toString(), jsonString))
      key = key + 1
      producer.commitTransaction()
    } catch {
      case e: KafkaException => 
        producer.abortTransaction()
        throw e
      case e: ProducerFencedException => throw e
      case e: AuthorizationException => throw e
    }
}

object EventProducer {
  def resource: Resource[IO, EventProducer] =
    Resource.make {
      println("Info: Opening event producer")
      IO(new EventProducer()).flatTap(_.open())
    } {
      service => service.close()
    }
}