package protocols

import play.api.libs.json.{Json, OFormat}
import utils.Date2SqlDate

import java.util.Date

object ExampleProtocol {


  case object GetList

  case class Delete(id: Int)

  case class Update(update: Example)

  case class Create(data: Example)

  case class Example(id: Option[Int] = None,
                     name: String,
                     tel: String,
                     age: String,
                     address: String
                    )
  implicit val studentFormat: OFormat[Example] = Json.format[Example]

  case class Documents(id: Option[Int] = None, createAt: Date2SqlDate, section: String, documentType: String, subDocumentType: String)

  implicit val documentFormat: OFormat[Documents] = Json.format[Documents]
}

