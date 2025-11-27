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
  import profile.api.*
  val table: TableQuery[AccountsTable]

  def queryAccountsByUsername(username: String): Option[Account] = 
    def setupQuery(username: String) = 
      table.filter(_.username === username)
    exec(setupQuery(username).result).headOption
}

trait ItemsDatabase extends Database with Tables {
  import profile.api.*
  val table: TableQuery[ItemsTable]

  def queryAllItems(): Seq[Item] =
    def setupQuery() =
      println(table)
      table
    val action = setupQuery().result
    println(action.statements.mkString)
    val items = exec(action)
    println(items)
    items
}

trait LoansDatabase extends Database with Tables {
  val table: TableQuery[LoansTable]
}

trait DatabaseReader {
  val accountsDatabase: AccountsDatabase
  val itemsDatabase: ItemsDatabase
  val loansDatabase: LoansDatabase
}