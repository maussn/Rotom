package nl.sogyo.apigateway

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.comcast.ip4s.*
import org.http4s.ember.server.*
import nl.sogyo.persistence.MySQLDatabaseReader
import nl.sogyo.kafka.EventProducer
import cats.effect.Resource
import org.http4s.server.Server

object Main extends IOApp:

  val databaseProvider = MySQLDatabaseReader

  val serverResource : Resource[IO, Server] =
    for {
      eventProducer <- EventProducer.resource
      services = GatewayServices.getServices(
        databaseReader = databaseProvider,
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
    serverResource.useForever
