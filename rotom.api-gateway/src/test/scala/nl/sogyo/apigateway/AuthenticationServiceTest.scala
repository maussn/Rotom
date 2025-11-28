package nl.sogyo.apigateway

import cats.data.Kleisli
import cats.effect.*
import io.circe.*
import io.circe.literal.*
import io.circe.generic.auto.*
import munit.CatsEffectSuite
import nl.sogyo.apigateway.GatewayServices.getServices
import nl.sogyo.persistence.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.*
import slick.jdbc.H2Profile.api.*

import scala.concurrent.duration.*
import java.util.UUID


class AuthenticationServiceTest extends CatsEffectSuite {

  //Setup
  override def munitIOTimeout: Duration =
    if (isDebugging) Duration.Inf
    else 30.seconds

  private def isDebugging: Boolean =
    java.lang.management.ManagementFactory
      .getRuntimeMXBean
      .getInputArguments
      .toString
      .contains("jdwp")

  val service = new Fixture[Kleisli[IO, Request[IO], Response[IO]]]("service") {
    var service: Kleisli[IO, Request[IO], Response[IO]] = null
    val dbReader = H2DatabaseReader
    override def apply(): Kleisli[IO, Request[IO], Response[IO]] = service
    override def beforeEach(context: BeforeEach): Unit = 
      createAccountsTable(dbReader.accountsDatabase)
      insertTestAccount(dbReader.accountsDatabase)
      service = getServices(dbReader)
    override def afterEach(context: AfterEach): Unit = 
      val resetDatabaseQuery = sqlu"""DROP ALL OBJECTS"""
      dbReader.accountsDatabase.exec(resetDatabaseQuery): Unit
  }
  override def munitFixtures = List(service)

  val correctUuid = UUID.randomUUID()
  val correctUsername = "jan"
  val correctPassword = "correctpassword"

  implicit val loginDecoder: EntityDecoder[IO, SuccessfulLogin] = jsonOf[IO, SuccessfulLogin]

  def createAccountsTable(db: AccountsDatabase) =
    val createAction = db.table.schema.create
    db.exec(createAction)
    

  def insertTestAccount(db: AccountsDatabase) = 
    val insertAction = (db.table += Account(correctUuid, correctUsername, correctPassword, true)).map(_ => ())
    db.exec(insertAction)
  
  // Tests
  test("test authentication service success") {
    val jsonBody = json"""{"username": ${correctUsername}, "password": ${correctPassword}}"""
    val loginRequest = Request[IO](Method.POST, uri"/api/login").withEntity(jsonBody)
    val response = service().run(loginRequest)
    for {
      _ <- assertIO(response.map(_.status.code), 200)
      body  <- response.flatMap(_.as[SuccessfulLogin])
      _ <- IO(assert(body.userId == correctUuid))
    } yield ()
  }

  test("test authentication service wrong password") {
    val wrongPassword = "wrongpassword"
    val jsonBody = json"""{"username": ${correctUsername}, "password": ${wrongPassword}}"""
    val loginRequest = Request[IO](Method.POST, uri"/api/login").withEntity(jsonBody)
    val response = service().run(loginRequest)
    for {
      _ <- assertIO(response.map(_.status.code), 401)
      _ <- assertIO(response.flatMap(_.as[String]), "Incorrect password.")
    } yield ()
  }

  test("test authentication service wrong username") {
    val wrongUsername = "klaas"
    val jsonBody = json"""{"username": ${wrongUsername}, "password": ${correctPassword}}"""
    val loginRequest = Request[IO](Method.POST, uri"/api/login").withEntity(jsonBody)
    val response = service().run(loginRequest)
    for {
      _ <- assertIO(response.map(_.status.code), 401)
      _ <- assertIO(response.flatMap(_.as[String]), "Incorrect username.")
    } yield ()
  }
}
