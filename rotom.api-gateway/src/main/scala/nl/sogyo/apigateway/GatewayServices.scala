package nl.sogyo.apigateway

import cats.data.Kleisli
import cats.effect.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.generic.semiauto
import nl.sogyo.apigateway.Authentication.authenticate
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import java.util.UUID
import nl.sogyo.persistence.Item
import nl.sogyo.persistence.DatabaseReader

case class UserLogin(username: String, password: String)
implicit val decoder: EntityDecoder[IO, UserLogin] = jsonOf[IO, UserLogin]

case class SuccessfulLogin(userId: UUID)
implicit val loginEncoder: Encoder[SuccessfulLogin] = semiauto.deriveEncoder[SuccessfulLogin]
implicit def loginEntityEncoder[F[_]]: EntityEncoder[F, SuccessfulLogin] = jsonEncoderOf[F, SuccessfulLogin]

case class ItemList(items: Seq[Item])
implicit val itemListEncoder: Encoder[ItemList] = semiauto.deriveEncoder[ItemList]
implicit def itemListEntityEncoder[F[_]]: EntityEncoder[F, ItemList] = jsonEncoderOf[F, ItemList]

val Api = Root / "api"

object GatewayServices:

  def getServices(databaseReader: DatabaseReader): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
    case req @ POST -> Api / "login" =>
      for {
        user <- req.as[UserLogin]
        account = databaseReader.queryAccountByUsername(user.username)
        auth = Try(authenticate(user, account))
        resp <- auth match
          case Success(userId) => Ok(SuccessfulLogin(userId))
          case Failure(e) => IO.pure(Response[IO](Status.Unauthorized).withEntity(e.getMessage()))
      } yield resp
    case req @ GET -> Api / "catalogue" =>
      val items = databaseReader.queryAllItems()
      Ok(ItemList(items))
    // case req @ GET -> Api / "catalogue" / userId => 
    //   val items = databaseProvider.itemsDatabase.queryItemsByUserId(UUID.fromString(userId))
    //   Ok(ItemList(items))
    }.orNotFound