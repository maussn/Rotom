package nl.sogyo.kafka

import cats.effect.*
import com.typesafe.scalalogging.Logger
import io.circe.syntax.*
import io.circe.generic.auto.*
import nl.sogyo.persistence.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthorizationException
import org.apache.kafka.common.errors.ProducerFencedException

import java.util.Properties
import java.util.UUID

class EventProducer extends IEventProducer:

  val logger = Logger(getClass.getName)

  private def setupProperties(): Properties =
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional-id-1")
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    // props.put("acks","all")
    props

  val props = setupProperties()
  val producer = new KafkaProducer[String, String](props)
  val topicNewLoans = "new_loans"
  var key = 0

  override def open: IO[Unit] = IO(producer.initTransactions())

  override def close: IO[Unit] = IO(producer.close())

  override def sendLoanRequestEvent(loanRequest: LoanRequest): Unit = 
    logger.info(s"Posting loan request event.")
    try {
      val loan = loanRequest match
      case LoanRequest(item, borrower, dateStart, dateEnd) => Loan(
        id = UUID.randomUUID(),
        item,
        borrower,
        dateStart,
        dateEnd, 
        dateReturned = None
      )
        
      val jsonString: String = loan.asJson.noSpaces
      producer.beginTransaction()
      producer.send(new ProducerRecord[String, String](topicNewLoans, key.toString(), jsonString))
      key = key + 1
      producer.commitTransaction()
    } catch {
      case e: KafkaException => 
        producer.abortTransaction()
        throw e
      case e: ProducerFencedException => throw e
      case e: AuthorizationException => throw e
      case e => throw e
    }


object EventProducer:
  def resource: Resource[IO, EventProducer] =
    Resource.make {
      IO(new EventProducer()).flatTap(_.open)
    } {
      service => service.close
    }