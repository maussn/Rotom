package nl.sogyo.persistence

import nl.sogyo.persistence.ProfileProvider
import java.util.UUID

case class Account(
  id: UUID,
  username: String,
  password: String
)

trait Tables { this: ProfileProvider =>
  import profile.api.*

  class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def id = column[UUID]("user_id", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def password = column[String]("auth_string")
    def * = (id, username, password).mapTo[Account]
  }

  val accounts = TableQuery[AccountsTable]
}
