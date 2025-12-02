package nl.sogyo.apigateway

import cats.data.Kleisli
import cats.effect.*
import io.circe.*
import io.circe.literal.*
import munit.CatsEffectSuite
import nl.sogyo.apigateway.GatewayServices.getServices
import nl.sogyo.persistence.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.*
import slick.jdbc.H2Profile.api.*

import java.util.UUID
import scala.concurrent.duration.*
import java.time.ZoneOffset
import java.time.LocalDate
import java.time.Month

class LoanServiceTest extends CatsEffectSuite {

  // Making sure there is no IO timeout while debugging

  override def munitIOTimeout: Duration =
    if (isDebugging) Duration.Inf
    else 30.seconds

  private def isDebugging: Boolean =
    java.lang.management.ManagementFactory
      .getRuntimeMXBean
      .getInputArguments
      .toString
      .contains("jdwp")

  // Setting up service
  
  val service = new Fixture[Kleisli[IO, Request[IO], Response[IO]]]("service") {
    var service: Kleisli[IO, Request[IO], Response[IO]] = null
    val dbReader = H2DatabaseReader
    override def apply(): Kleisli[IO, Request[IO], Response[IO]] = service
    override def beforeEach(context: BeforeEach): Unit = 
      setupAccountsTable(dbReader)
      setupItemsTable(dbReader)
      service = getServices(dbReader)
    override def afterEach(context: AfterEach): Unit = 
      val resetDatabaseQuery = sqlu"""DROP ALL OBJECTS"""
      dbReader.exec(resetDatabaseQuery): Unit
  }

  override def munitFixtures = List(service)

  // Setup accounts database
  
  def createAccountsTable(dbReader: DatabaseReader) =
    val createAction = dbReader.accountsTable.schema.create
    dbReader.exec(createAction)
    

  def insertTestAccount(dbReader: DatabaseReader, account: Account) = 
    val insertAction = (dbReader.accountsTable += account).map(_ => ())
    dbReader.exec(insertAction)

  def setupAccountsTable(dbReader: DatabaseReader) =
      createAccountsTable(dbReader)
      insertTestAccount(dbReader, jan)
      insertTestAccount(dbReader, piet)

  // Setup items database

  def createItemsDatabase(dbReader: DatabaseReader) = 
    val createAction = dbReader.itemsTable.schema.create
    dbReader.exec(createAction)

  def insertTestItem(dbReader: DatabaseReader, item: Item) =
    val insertAction = (dbReader.itemsTable += item).map(_ => ())
    dbReader.exec(insertAction)
  
  def setupItemsTable(dbReader: DatabaseReader) =
    createItemsDatabase(dbReader)
    insertTestItem(dbReader, drill)

  // Test data
  val jan = Account(
    id = UUID.randomUUID(),
    username = "jan",
    password = "pw1",
    active = true
  )

  val piet = Account(
    id = UUID.randomUUID(),
    username = "piet",
    password = "pw2",
    active = true
  )

  val drill = Item(
    id = UUID.randomUUID(),
    owner = piet.id,
    name = "Drill",
    description = None,
    active = true
  )

  // Utils

  def loanRequestToJson(req: LoanRequest) =
    json"""{
      "item": ${req.item.toString()},
      "borrower": ${req.borrower.toString()},
      "dateStart": ${req.dateStart.toString()},
      "dateEnd": ${req.dateEnd.toString()}
    }"""

  // Tests

  test("Test request loan with incorrect dates returns Bad Request") {
    val requestObject = LoanRequest(
      item = drill.id,
      borrower = jan.id,
      dateStart = LocalDate.of(2001, Month.JANUARY, 1).atStartOfDay().atOffset(ZoneOffset.UTC),
      dateEnd = LocalDate.of(2000, Month.JANUARY, 1).atStartOfDay().atOffset(ZoneOffset.UTC)
    )
    val jsonBody = loanRequestToJson(requestObject)
    val apiRequest = Request[IO](Method.POST, uri"/api/loan").withEntity(jsonBody)
    val response = service().run(apiRequest)
    for {
      _ <- assertIO(response.map(_.status.code), 400)
      _ <- assertIO(response.flatMap(_.as[String]), StartDateAfterEndDateException().getMessage())
    } yield ()
  }

  test("Test 2") {
    val id = "a801bd21-ceb3-11f0-8ee0-40a8f04649c1"
    val borrower = "a7fb9549-ceb3-11f0-8ee0-40a8f04649c1"
    val dateStart = "2025-12-05T11:27:12.092Z"
    val dateEnd = "2025-12-02T11:27:12.092Z"
    val body = json"""{
      "item": ${id},
      "borrower": ${borrower},
      "dateStart": ${dateStart},
      "dateEnd": ${dateEnd}
    }"""
    val apiRequest = Request[IO](Method.POST, uri"/api/loan").withEntity(body)
    val response = service().run(apiRequest)
    for {
      _ <- assertIO(response.map(_.status.code), 400)
      _ <- assertIO(response.flatMap(_.as[String]), StartDateAfterEndDateException().getMessage())
    } yield ()
  }

  test("Test casting of dates.") {
    val id = "a801bd21-ceb3-11f0-8ee0-40a8f04649c1"
    val borrower = "a7fb9549-ceb3-11f0-8ee0-40a8f04649c1"
    val dateStart = "2025-12-05T11:27:12.092Z"
    val dateEnd = "2025-12-02T11:27:12.092Z"
    val body = json"""{
      "item": ${id},
      "borrower": ${borrower},
      "dateStart": ${dateStart},
      "dateEnd": ${dateEnd}
    }"""
    val apiRequest = Request[IO](Method.POST, uri"/api/loan").withEntity(body)
    for {
      loan <- apiRequest.as[LoanRequest]
      t = print(loan)
      t1 = print(loan.dateStart)
      t2 = print(loan.dateEnd)
      t3 = assert(loan.dateStart.isAfter(loan.dateEnd))
    } yield()
  }

  test("Test LoanProcessor") {
    
  }
}