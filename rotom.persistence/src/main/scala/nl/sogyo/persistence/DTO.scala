package nl.sogyo.persistence

import java.time.LocalDateTime
import java.util.UUID
import cats.effect.IO
import io.circe.*
import io.circe.generic.auto.*
import io.circe.generic.semiauto
import org.http4s.*
import org.http4s.circe.*
import nl.sogyo.persistence.Item

case class UserLogin(username: String, password: String)
implicit val decoderUserLogin: EntityDecoder[IO, UserLogin] = jsonOf[IO, UserLogin]

case class SuccessfulLogin(userId: UUID)
implicit val encoderSuccessfulLogin: Encoder[SuccessfulLogin] = semiauto.deriveEncoder[SuccessfulLogin]
implicit def entityEncoderSuccessfulLogin[F[_]]: EntityEncoder[F, SuccessfulLogin] = jsonEncoderOf[F, SuccessfulLogin]

case class ItemList(items: Seq[Item])
implicit val encoderItemList: Encoder[ItemList] = semiauto.deriveEncoder[ItemList]
implicit def entityEncoderItemList[F[_]]: EntityEncoder[F, ItemList] = jsonEncoderOf[F, ItemList]

case class LoanRequest(item: UUID, borrower: UUID, dateStart: LocalDateTime, dateEnd: LocalDateTime)
implicit val decoderLoanRequest: EntityDecoder[IO, LoanRequest] = jsonOf[IO, LoanRequest]