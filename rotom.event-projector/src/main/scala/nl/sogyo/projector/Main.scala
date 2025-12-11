package nl.sogyo.projector

import com.typesafe.scalalogging.Logger
import nl.sogyo.persistence.MySQLDatabaseWriter
import nl.sogyo.kafka.EventConsumer


object Main:

  val logger = Logger(getClass.getName)

  val databaseWriter = MySQLDatabaseWriter
  val eventConsumer = EventConsumer()

  @main def app(): Unit = 
    eventConsumer.run(databaseWriter)
