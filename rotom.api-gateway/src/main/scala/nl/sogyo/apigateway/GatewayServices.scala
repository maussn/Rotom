package nl.sogyo.apigateway

import cats.data.Kleisli
import cats.effect.*
import nl.sogyo.apigateway.Authentication.authenticate
import nl.sogyo.persistence.*
import org.http4s.*
import org.http4s.dsl.io.*

import scala.util.Failure
import scala.util.Success
import scala.util.Try

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
    // case req @ POST -> Api / "loan" =>
    //   for {
    //     loan <- req.as[LoanRequest]
    //     result = Try(processLoanRequest(loan, databaseReader))
    //     resp <- Ok()
    //   } yield(resp)
    }.orNotFound