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
import com.typesafe.scalalogging.Logger
import nl.sogyo.apigateway.Main.databaseProvider

val Api = Root / "api"

object GatewayServices:

  val logger = Logger(getClass.getName)

  def getServices(databaseReader: DatabaseReader, eventProducer: EventProducer): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
    case req @ POST -> Api / "login" =>
      handleLoginRequest(req, databaseReader)

    case req @ GET -> Api / "catalogue" =>
      handleCatalogueRequest(databaseProvider)

    case req @ GET -> Api / "catalogue" / userId =>
      handleCatalogueRequestLoggedIn(userId, databaseReader)

    case req @ POST -> Api / "loan" =>
      handleLoanRequest(req, databaseReader, eventProducer)

    case req @ GET -> Api / "test" =>
      databaseReader.queryJoinItemsWithLoans()
      Ok()
    }.orNotFound


  def handleCatalogueRequest(databaseReader: DatabaseReader) =
    logger.info("Catalogue GET received (not logged in).")
    val items = databaseReader.queryAllAvailableItems()
    Ok(ItemList(items))

  
  def handleCatalogueRequestLoggedIn(userId: String, databaseReader: DatabaseReader) =
    logger.info("Catalogue GET received (logged in).")
    val items = databaseReader.queryCatalogueLoggedInUser(UUID.fromString(userId))
    Ok(ItemList(items))


  def handleLoginRequest(request: Request[IO], databaseReader: DatabaseReader) =
    logger.info("Login request received.")
    for {
      user <- request.as[UserLogin]
      account = databaseReader.queryAccountByUsername(user.username)
      auth = Try(authenticate(user, account))
      resp <- auth match
        case Success(userId) => Ok(SuccessfulLogin(userId))
        case Failure(e) => IO.pure(Response[IO](Status.Unauthorized).withEntity(e.getMessage()))
    } yield resp


  def handleLoanRequest(request: Request[IO], databaseReader: DatabaseReader, eventProducer: EventProducer) = 
    logger.info("Loan request received.")
    request.as[LoanRequest].attempt.flatMap {
      case Right(loanRequest) => 
        val loanTry = Try(processLoanRequest(loanRequest, databaseReader))
        loanTry match
          case Failure(exception) => 
            logger.warn(s"Invalid data in loan request: cause = ${exception.toString()}")
            BadRequest(exception.getMessage())
          case Success(_) => 
            val result = Try(eventProducer.sendLoanRequestEvent(loanRequest))
            result match
              case Failure(exception) => 
                logger.error(s"Failed to post loan request event: " +
                  s"cause = ${exception.toString()} " +
                  s"message = ${exception.getMessage()}")
                InternalServerError(exception.getMessage())
              case Success(_) => Ok()
      case Left(exception) => 
        logger.warn(s"Invalid request body received on /api/loan.")
        BadRequest(exception.getMessage())
    }