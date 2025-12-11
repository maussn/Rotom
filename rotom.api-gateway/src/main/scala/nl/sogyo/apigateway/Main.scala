package nl.sogyo.apigateway

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.comcast.ip4s.*
import nl.sogyo.kafka.EventProducer
import nl.sogyo.persistence.MySQLDatabaseReader
import org.http4s.ember.server.*

object Main extends IOApp:

  val databaseReader = MySQLDatabaseReader

  def getServerResource() = 
    for {
      eventProducer <- EventProducer.resource
      services = GatewayServices.getServices(
        databaseReader = databaseReader,
        eventProducer = eventProducer 
      )
      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(services)
        .build
    } yield server


  def run(args: List[String]): IO[ExitCode] =
    getServerResource().useForever
