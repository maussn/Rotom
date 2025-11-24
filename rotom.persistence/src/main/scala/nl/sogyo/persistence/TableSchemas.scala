package nl.sogyo.persistence

import nl.sogyo.persistence.ProfileProvider
import java.util.UUID

case class Account(
  uuid: UUID,
  username: String,
  password: String
)

trait Tables { this: ProfileProvider =>
  import profile.api.*

  class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def password = column[String]("auth_string")
    def * = (uuid, username, password).mapTo[Account]
  }

  val accounts = TableQuery[AccountsTable]
}
