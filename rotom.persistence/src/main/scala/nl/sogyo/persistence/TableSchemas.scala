package nl.sogyo.persistence

import nl.sogyo.persistence.ProfileProvider

import java.time.LocalDateTime
import java.util.UUID

case class Account(
  id: UUID,
  username: String,
  password: String,
  active: Boolean
)

case class Item(
  id: UUID,
  owner: UUID,
  name: String,
  description: Option[String]
)

case class Loan(
  id: UUID,
  item: UUID,
  borrower: UUID,
  dateStart: LocalDateTime,
  dateEnd: LocalDateTime,
  dateReturned: Option[LocalDateTime]
)

trait Tables { this: ProfileProvider =>
  import profile.api.*

  class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def id = column[UUID]("user_id", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def password = column[String]("auth_string")
    def active = column[Boolean]("is_active")
    def * = (id, username, password, active).mapTo[Account]
  }

  val accounts = TableQuery[AccountsTable]

  class ItemsTable(tag: Tag) extends Table[Item](tag, "items") {
    def id = column[UUID]("item_id", O.PrimaryKey)
    def owner = column[UUID]("owner_id")
    def name = column[String]("item_name")
    def description = column[Option[String]]("item_description")
    def ownerFk = foreignKey("fk_items_owner", owner, accounts)(_.id, onDelete = ForeignKeyAction.Cascade)
    def * = (id, owner, name, description).mapTo[Item]
  }

  val items = TableQuery[ItemsTable]

  class LoansTable(tag: Tag) extends Table[Loan](tag, "loans") {
    def id = column[UUID]("loan_id", O.PrimaryKey)
    def item = column[UUID]("item_id")
    def borrower = column[UUID]("borrower_id")
    def dateStart = column[LocalDateTime]("loan_start")
    def dateEnd = column[LocalDateTime]("loan_end")
    def dateReturned = column[Option[LocalDateTime]]("loan_returned")
    def itemFk = foreignKey("fk_loans_item", item, items)(_.id, onDelete = ForeignKeyAction.Restrict)
    def borrowerFk = foreignKey("fk_loans_borrower", borrower, accounts)(_.id, onDelete = ForeignKeyAction.Restrict)
    def * = (id, item, borrower, dateStart, dateEnd, dateReturned).mapTo[Loan]
  }

  val loans = TableQuery[LoansTable]
}
