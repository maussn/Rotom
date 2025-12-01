package nl.sogyo.persistence

import slick.dbio.DBIO
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.*
import java.util.UUID

trait ProfileProvider {
  val profile: slick.jdbc.JdbcProfile
}

trait Database extends ProfileProvider with Tables {
  val db: profile.backend.JdbcDatabaseDef

  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2.seconds)

}

trait AccountsDatabase extends Database with Tables {
  val table: TableQuery[AccountsTable]
}

trait ItemsDatabase extends Database with Tables {
  val table: TableQuery[ItemsTable]
}

trait LoansDatabase extends Database with Tables {
  val table: TableQuery[LoansTable]
}

trait DatabaseReader {
  val profile: slick.jdbc.JdbcProfile
  val accountsDatabase: AccountsDatabase
  val itemsDatabase: ItemsDatabase
  val loansDatabase: LoansDatabase

  import profile.api.* 

  def queryAllItems(): Seq[Item] =
    val query = itemsDatabase.table
    itemsDatabase.exec(query.result)

  def queryAccountByUsername(username: String): Option[Account] =
    val query = accountsDatabase.table.filter(_.username === username)
    val result = accountsDatabase.exec(query.result)
    if result.size > 1 then throw NonUniqueUsernameException(username)
    result.headOption

  def queryItemById(itemId: UUID): Option[Item] = 
    val query = itemsDatabase.table.filter(_.id === itemId)
    val result = itemsDatabase.exec(query.result)
    if result.size > 1 then throw NonUniqueUUIDException(itemId)
    result.headOption

  // def queryAvailableItemById(itemId: UUID): Option[Item] =
  //   val query = 
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
  s"Found multiple entries for uuid=$uuid. This should not be thrown.", cause
)