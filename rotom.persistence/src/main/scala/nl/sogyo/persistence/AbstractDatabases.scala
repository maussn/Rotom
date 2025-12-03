package nl.sogyo.persistence

import slick.dbio.DBIO
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.*
import java.util.UUID
import com.typesafe.scalalogging.Logger

trait ProfileProvider {
  val profile: slick.jdbc.JdbcProfile
}

trait DatabaseReader extends ProfileProvider with Tables {
  val logger = Logger(getClass.getName)

  val db: profile.backend.JdbcDatabaseDef

  val accountsTable = TableQuery[AccountsTable]
  val itemsTable = TableQuery[ItemsTable]
  val loansTable = TableQuery[LoansTable]

  import profile.api.* 

  def exec[T](action: DBIO[T]): T =
    logger.debug(s"Querying database: action = ${action}")
    Await.result(db.run(action), 2.seconds)

  def queryAllItems(): Seq[Item] =
    val query = itemsTable
    exec(query.result)

  def queryAllAvailableItems(): Seq[Item] =
    val query = itemsTable.filterNot {
      item => !item.active ||
        loansTable.filter(loan => loan.item === item.id && loan.dateReturned.isEmpty).exists
    }
    exec(query.result)
  
  def queryCatalogueLoggedInUser(userId: UUID): Seq[Item] = 
    val query = itemsTable.filterNot {
      item => !item.active ||
        loansTable.filter(loan => loan.item === item.id && loan.dateReturned.isEmpty).exists ||
        item.owner === userId
    }
    exec(query.result)

  def queryAccountByUsername(username: String): Option[Account] =
    val query = accountsTable.filter(_.username === username)
    val result = exec(query.result)
    if result.size > 1 then throw NonUniqueUsernameException(username)
    result.headOption

  def queryItemById(itemId: UUID): Option[Item] = 
    val query = itemsTable.filter(_.id === itemId)
    val result = exec(query.result)
    if result.size > 1 then throw NonUniqueUUIDException(itemId)
    result.headOption

  def queryJoinItemsWithLoans(): Unit =
    val query = for {
      (loans, items) <- loansTable join itemsTable on (_.item === _.id)
    } yield (loans, items)
    print(exec(query.result))

  def queryActiveLoan(itemId: UUID): Option[Loan] =
    val query = for {
      (loan, item) <- loansTable join itemsTable on (_.item === _.id)
      if item.id === itemId
      if loan.dateReturned.isEmpty
    } yield loan
    val result = exec(query.result)
    if result.size > 1 then throw MultipleActiveLoansException(itemId)
    result.headOption

  
}

class MultipleEntriesException(
  private val message: String,
  private val cause: Throwable = None.orNull
) extends Exception(message, cause)

case class NonUniqueUsernameException(
  private val username: String,
  private val cause: Throwable = None.orNull
) extends MultipleEntriesException(
  s"Multiple accounts assigned to same username=$username. This should not be thrown.",
  cause
)

case class NonUniqueUUIDException(
  private val uuid: UUID,
  private val cause: Throwable = None.orNull
) extends MultipleEntriesException(
  s"Found multiple entries for uuid=$uuid. This should not be thrown.",
  cause
)

case class MultipleActiveLoansException(
  private val uuid: UUID,
  private val cause: Throwable = None.orNull
) extends MultipleEntriesException(
  s"Found multiple active loans for item uuid=$uuid. This should not be thrown",
  cause
)