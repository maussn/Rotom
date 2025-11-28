package nl.sogyo.kafka

import nl.sogyo.persistence.*
import org.apache.kafka.clients.producer.KafkaProducer

import java.util.Properties


object EventProducers {
  val props: Properties = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks","all")
  
  val producer = new KafkaProducer[String, String](props)
  val topic = "new_loans"

  def sendLoanRequestEvent(loanRequest: LoanRequest) = ???
}