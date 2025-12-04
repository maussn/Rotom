package nl.sogyo.apigateway

import cats.effect.IO
import nl.sogyo.kafka.EventProducer
import nl.sogyo.persistence.LoanRequest
// import org.apache.kafka.clients.producer.KafkaProducer
// import java.{util => ju}
// import ju.Properties

class EventProducerMock extends EventProducer {
  override def open(): IO[Unit] = ???
  override def close(): IO[Unit] = ???
  override def sendLoanRequestEvent(loanRequest: LoanRequest): Unit = ???
  // override val producer: KafkaProducer[String, String] = ???
  // override val props: ju.Properties = new Properties()
}
