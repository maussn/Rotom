package nl.sogyo.kafka

import cats.effect.IO
import nl.sogyo.persistence.LoanRequest

class EventProducerMock extends IEventProducer :

  override def open: IO[Unit] = IO({})

  override def close: IO[Unit] = IO({})
  
  override def sendLoanRequestEvent(loanRequest: LoanRequest): Unit = {}
