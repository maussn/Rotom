package nl.sogyo.apigateway

import nl.sogyo.kafka.EventProducer
import cats.effect.IO
import nl.sogyo.persistence.Loan

class EventProducerMock extends EventProducer {
  override def open(): IO[Unit] = ???
  override def close(): IO[Unit] = ???
  override def sendLoanRequestEvent(loan: Loan): Unit = ???
}
