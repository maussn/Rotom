package nl.sogyo.persistence

import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery


object MySQLAccountsDatabase extends AccountsDatabase with Tables {
  override val profile = slick.jdbc.MySQLProfile
  override val table = TableQuery[AccountsTable]
  override val db = profile.api.Database.forConfig("rotom-root")
}

object MySQLItemsDatabase extends ItemsDatabase with Tables {
  override val profile = slick.jdbc.MySQLProfile
  override val table = TableQuery[ItemsTable]
  override val db = profile.api.Database.forConfig("rotom-root")
}

object MySQLLoansDatabase extends LoansDatabase with Tables {
  override val profile = slick.jdbc.MySQLProfile
  override val table = TableQuery[LoansTable]
  override val db = profile.api.Database.forConfig("rotom-root")
}

object MySQLDatabaseProvider extends DatabaseProvider {
  override val accountsDatabase = MySQLAccountsDatabase
  override val itemsDatabase = MySQLItemsDatabase
  override val loansDatabase = MySQLLoansDatabase
}