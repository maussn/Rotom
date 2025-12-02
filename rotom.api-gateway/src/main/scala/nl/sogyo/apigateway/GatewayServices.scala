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
import java.util.UUID
import nl.sogyo.apigateway.LoanProcessor.processLoanRequest
import nl.sogyo.kafka.EventProducer

val Api = Root / "api"


object GatewayServices:

  def getServices(databaseReader: DatabaseReader, eventProducer: EventProducer): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
    case req @ POST -> Api / "login" =>
      println("Info: Received login POST")
      for {
        user <- req.as[UserLogin]
        _ = println(user)
        account = databaseReader.queryAccountByUsername(user.username)
        auth = Try(authenticate(user, account))
        resp <- auth match
          case Success(userId) => Ok(SuccessfulLogin(userId))
          case Failure(e) => IO.pure(Response[IO](Status.Unauthorized).withEntity(e.getMessage()))
      } yield resp

    case req @ GET -> Api / "catalogue" =>
      println("Info: Received not logged in catalogue GET")
      val items = databaseReader.queryAllAvailableItems()
      Ok(ItemList(items))

    case req @ GET -> Api / "catalogue" / userId =>
      println("Info: Received logged in catalogue GET")
      val items = databaseReader.queryCatalogueLoggedInUser(UUID.fromString(userId))
      Ok(ItemList(items))

    case req @ POST -> Api / "loan" =>
      // Logging
      println("Info: Received loan request.")
      // 
      handleLoanRequest(req, databaseReader, eventProducer)
      // for {
      //   loanRequest <- req.as[LoanRequest]
      //   _ = println("Info: Parsed JSON to LoanRequest")
      //   loan = Try(processLoanRequest(loanRequest, databaseReader))
      //   resp <- loan match
      //     case Failure(exception) => BadRequest(exception.getMessage())
      //     case Success(loan) => Ok()
      //   _ = println("Info: Finished handling loan request")
      // } yield(resp)

    case req @ GET -> Api / "test" =>
      databaseReader.queryJoinItemsWithLoans()
      Ok()
    }.orNotFound

  def handleLoanRequest(request: Request[IO], databaseReader: DatabaseReader, eventProducer: EventProducer) = 
    request.as[LoanRequest].attempt.flatMap {
      case Right(loanRequest) => 
        val loanTry = Try(processLoanRequest(loanRequest, databaseReader))
        loanTry match
          case Failure(exception) => 
            println(s"One of the fields is not correct. ${exception.toString()} ${exception.getMessage()}")
            BadRequest(exception.getMessage())
          case Success(loan) => 
            val result = Try(eventProducer.sendLoanRequestEvent(loan))
            result match
              case Failure(exception) => 
                println(s"Info: Failed to post loan request event. ${exception.toString()}: ${exception.getMessage()}")
                InternalServerError(exception.getMessage())
              case Success(value) => Ok()
      case Left(exception) => 
        println("Info: Received bad request body")
        BadRequest(exception.getMessage())
    }