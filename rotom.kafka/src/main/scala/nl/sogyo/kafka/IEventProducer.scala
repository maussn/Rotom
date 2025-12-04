package nl.sogyo.kafka

import cats.effect.IO
import nl.sogyo.persistence.LoanRequest


trait IEventProducer:
  
  def open: IO[Unit]
  
  def close: IO[Unit]

  def sendLoanRequestEvent(loanRequest: LoanRequest): Unit
