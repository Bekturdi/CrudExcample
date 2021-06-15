package protocols

import play.api.libs.json.{Json, OFormat}

import java.util.Date

object ExampleProtocol {


  case object GetList

  case object GetDocuments

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

  case class Documents(id: Option[Int] = None,
                       createAt: Date,
                       section: String,
                       documentType: String,
                       subDocumentType: String
                      )
  case class CmdDocuments(documents: Documents)

  implicit val documentFormat: OFormat[Documents] = Json.format[Documents]

  case class GetDocumentsBySection(section: Option[String] = None)

  case class GetDocumentsByDocType(docType: String)

  case class ReportForm(
                       section: Option[String] = None,
                       documentType: Option[String] = None
                       )

  implicit val reportFormFormat: OFormat[ReportForm] = Json.format[ReportForm]
}

