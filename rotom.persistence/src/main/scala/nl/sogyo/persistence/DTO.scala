package nl.sogyo.persistence

import cats.effect.IO
import cats.syntax.either.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.generic.semiauto
import nl.sogyo.persistence.Item
import org.http4s.*
import org.http4s.circe.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

case class UserLogin(username: String, password: String)
implicit val decoderUserLogin: EntityDecoder[IO, UserLogin] = jsonOf[IO, UserLogin]

case class SuccessfulLogin(userId: UUID)
implicit val decoderSuccessfulLogin: EntityDecoder[IO, SuccessfulLogin] = jsonOf[IO, SuccessfulLogin]
implicit val encoderSuccessfulLogin: Encoder[SuccessfulLogin] = semiauto.deriveEncoder[SuccessfulLogin]
implicit def entityEncoderSuccessfulLogin[F[_]]: EntityEncoder[F, SuccessfulLogin] = jsonEncoderOf[F, SuccessfulLogin]

case class ItemList(items: Seq[Item])
implicit val encoderItemList: Encoder[ItemList] = semiauto.deriveEncoder[ItemList]
implicit def entityEncoderItemList[F[_]]: EntityEncoder[F, ItemList] = jsonEncoderOf[F, ItemList]

implicit val localDateTimeDecoder: Decoder[LocalDateTime] =
  Decoder.decodeString.emap {
    str =>
      val clean = str.stripSuffix("Z")
      Either.catchNonFatal(LocalDateTime.parse(clean)).left.map(_.getMessage)
  }
implicit val localDateTimeEncoder: Encoder[LocalDateTime] =
  Encoder.encodeString.contramap(_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

case class LoanRequest(item: UUID, borrower: UUID, dateStart: LocalDateTime, dateEnd: LocalDateTime)
implicit val decoderLoanRequest: EntityDecoder[IO, LoanRequest] = jsonOf[IO, LoanRequest]
implicit val encoderLoanRequest: Encoder[LoanRequest] = semiauto.deriveEncoder[LoanRequest]
implicit def entityEncoderLoanRequest[F[_]]: EntityEncoder[F, LoanRequest] = jsonEncoderOf[F, LoanRequest]