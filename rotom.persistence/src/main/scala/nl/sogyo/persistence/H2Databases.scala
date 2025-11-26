package nl.sogyo.persistence

import slick.lifted.TableQuery

object H2AccountsDatabase extends AccountsDatabase with Tables {
  override val profile = slick.jdbc.H2Profile
  override val table = TableQuery[AccountsTable]
  override val db = profile.api.Database.forConfig("H2-test")
}

object H2ItemsDatabase extends ItemsDatabase with Tables {
  override val profile = slick.jdbc.H2Profile
  override val table = TableQuery[ItemsTable]
  override val db = profile.api.Database.forConfig("H2-test")
}

object H2LoansDatabase extends LoansDatabase with Tables {
  override val profile = slick.jdbc.H2Profile
  override val table = TableQuery[LoansTable]
  override val db = profile.api.Database.forConfig("H2-test")
}

object H2DatabaseProvider extends DatabaseProvider {
  override val accountsDatabase = H2AccountsDatabase
  override val itemsDatabase = H2ItemsDatabase
  override val loansDatabase = H2LoansDatabase
}