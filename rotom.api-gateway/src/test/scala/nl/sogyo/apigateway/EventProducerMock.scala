package nl.sogyo.apigateway

import cats.effect.IO
import nl.sogyo.kafka.EventProducer
import nl.sogyo.persistence.LoanRequest

class EventProducerMock extends EventProducer {
  override def open(): IO[Unit] = ???
  override def close(): IO[Unit] = ???
  override def sendLoanRequestEvent(loanRequest: LoanRequest): Unit = ???
}
