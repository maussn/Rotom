package nl.sogyo.persistence

object MySQLDatabaseReader extends DatabaseReader {
  override val profile = slick.jdbc.MySQLProfile
  override val db = profile.api.Database.forConfig("rotom-root")
}