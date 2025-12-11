package nl.sogyo.kafka

import scala.jdk.CollectionConverters.*

object Topics {
  val newLoans = "new_loans"

  val topics: java.util.List[String] = List(newLoans).asJava
}
