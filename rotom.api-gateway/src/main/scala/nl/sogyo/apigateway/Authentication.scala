package nl.sogyo.apigateway

import nl.sogyo.persistence.*
import java.util.UUID
import com.typesafe.scalalogging.Logger

object Authentication {

  val logger = Logger(getClass.getName)

  private def isCorrectPassword(passwordDatabase: String, passwordRequest: String): Boolean =
    passwordDatabase.equals(passwordRequest)
    
  private def checkPassword(account: Account, userLogin: UserLogin): UUID =
    if isCorrectPassword(account.password, userLogin.password)
    then account.id
    else 
      logger.debug("Password mismatch.")
      throw IncorrectLoginException("Incorrect password.")

  def authenticate(userLogin: UserLogin, accountOption: Option[Account]): UUID =
    logger.debug(s"Authenticating login: username = ${userLogin.username}")
    accountOption match
      case Some(account) => checkPassword(account, userLogin)
      case None => 
        logger.debug(s"Username not found.")
        throw IncorrectLoginException("Incorrect username.")
}

final case class IncorrectLoginException(
  private val message: String = "",
  private val cause: Throwable = None.orNull
) extends Exception(message, cause)

final case class MissingUuidException(
  private val message: String = "This should not be thrown.",
  private val cause: Throwable = None.orNull
) extends Exception(message, cause)