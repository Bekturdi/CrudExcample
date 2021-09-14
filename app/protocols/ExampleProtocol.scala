package protocols

import play.api.libs.json.{Json, OFormat}

import java.util.Date

object ExampleProtocol {

  case class Delete(id: Int)

  case class Update(update: Example)

  case class Create(data: Example)

  case class Example(id: Option[Int] = None,
    name: String,
    tel: String,
    age: String,
    address: String
  )

  case class Documents(id: Option[Int] = None,
    createAt: Date,
    section: String,
    documentType: String,
    subDocumentType: String,
    group: Option[String] = None,
    executive: Option[String] = None
  )

  case class CmdDocuments(documents: Documents)

  implicit val studentFormat: OFormat[Example] = Json.format[Example]

  case class GetDocumentsBySection(section: Option[String] = None)

  case class GetDocumentsByDocType(docType: String)

  implicit val documentFormat: OFormat[Documents] = Json.format[Documents]

  case class ReportForm(
                         section: Option[String] = None,
                         documentType: Option[String] = None
                       )

  case class DeleteDocuments(id: Int)

  case class EquipmentNumber(number: String)

  implicit val reportFormFormat: OFormat[ReportForm] = Json.format[ReportForm]

  case object GetList

  case object GetDocuments

  implicit val equipmentNumberFormat: OFormat[EquipmentNumber] = Json.format[EquipmentNumber]
}

