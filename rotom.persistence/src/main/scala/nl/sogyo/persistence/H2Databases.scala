package nl.sogyo.persistence

object H2DatabaseReader extends DatabaseReader {
  override val profile = slick.jdbc.H2Profile
  override val db = profile.api.Database.forConfig("H2-test")
}

object H2DatabaseWriter extends DatabaseWriter {
  override val profile = slick.jdbc.H2Profile
  override val db = profile.api.Database.forConfig("H2-test")
}