package nl.sogyo.persistence

import slick.dbio.DBIO
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.*

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
    def setupQuery() =
      itemsDatabase.table
    itemsDatabase.exec(setupQuery().result)

  def queryAccountsByUsername(username: String): Option[Account] =
    def setupQuery(username: String) = 
      accountsDatabase.table.filter(_.username === username)
    accountsDatabase.exec(setupQuery(username).result).headOption
}