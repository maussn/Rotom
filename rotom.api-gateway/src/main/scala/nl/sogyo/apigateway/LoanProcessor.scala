package nl.sogyo.apigateway

import nl.sogyo.persistence.*
import java.util.UUID
import com.typesafe.scalalogging.Logger


object LoanProcessor:

  val logger = Logger(getClass.getName)

  private def checkStartDate(request: LoanRequest): Unit =
    if request.dateStart.isAfter(request.dateEnd) then
      throw StartDateAfterEndDateException()

  private def checkIfItemExists(request: LoanRequest, dbReader: DatabaseReader): Unit =
    val itemOption = dbReader.queryItemById(request.item)
    itemOption match
      case Some(item) => 
      case None => throw ItemNotFoundException(request.item)
    

  def checkIfItemIsAvailable(request: LoanRequest, dbReader: DatabaseReader): Unit =
    val loanOption = dbReader.queryActiveLoan(request.item)
    loanOption match
      case Some(loan) => throw ItemNotAvailableException(request.item)
      case None =>

  private def checkItem(request: LoanRequest, dbReader: DatabaseReader): Unit = 
    checkIfItemExists(request, dbReader)
    checkIfItemIsAvailable(request, dbReader)

  def processLoanRequest(request: LoanRequest, dbReader: DatabaseReader): Unit = 
    checkStartDate(request)
    checkItem(request, dbReader)
      

final case class StartDateAfterEndDateException(
  private val message: String = "Given start date is after given end date.",
  private val cause: Throwable = None.orNull
) extends Exception(message, cause)

final case class ItemNotAvailableException(
  private val uuid: UUID,
  private val cause: Throwable = None.orNull
) extends Exception(s"Requested item is not available. uuid=$uuid", cause)

final case class ItemNotFoundException(
  private val uuid: UUID,
  private val cause: Throwable = None.orNull
) extends Exception(s"Could not find requested item. uuid=$uuid", cause)