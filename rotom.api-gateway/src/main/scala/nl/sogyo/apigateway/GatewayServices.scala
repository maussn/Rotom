package nl.sogyo.apigateway

import cats.data.Kleisli
import cats.effect.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.generic.semiauto
import nl.sogyo.apigateway.Authentication.authenticate
import nl.sogyo.persistence.DatabaseProvider
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import java.util.UUID

case class UserLogin(username: String, password: String)
implicit val decoder: EntityDecoder[IO, UserLogin] = jsonOf[IO, UserLogin]

case class SuccessfulLogin(uuid: UUID)
implicit val loginEncoder: Encoder[SuccessfulLogin] = semiauto.deriveEncoder[SuccessfulLogin]
implicit def loginEntityEncoder[F[_]]: EntityEncoder[F, SuccessfulLogin] = jsonEncoderOf[F, SuccessfulLogin]

object GatewayServices:

  def getServices(databaseProvider: DatabaseProvider): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
    case req @ POST -> Root / "api" / "login" =>
      for {
        user <- req.as[UserLogin]
        auth = Try(authenticate(user, databaseProvider.accountsDatabase))
        resp <- auth match
          case Success(uuid) => Ok(SuccessfulLogin(uuid))
          case Failure(e) => IO.pure(Response[IO](Status.Unauthorized).withEntity(e.getMessage()))
      } yield resp
    }.orNotFound