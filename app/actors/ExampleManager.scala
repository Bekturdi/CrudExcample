package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao._
import play.api.Environment
import protocols.ExampleProtocol._

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class ExampleManager @Inject()(val environment: Environment,
                               exampleDao: ExampleDao,
                               documentsDao: DocumentsDao
                              )
                              (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive: Receive = {
    case Create(data) =>
      create(data).pipeTo(sender())

    case Update(data) =>
      update(data).pipeTo(sender())

    case Delete(id) =>
      delete(id).pipeTo(sender())

    case GetList =>
      getList.pipeTo(sender())

    case CmdDocuments(documents) =>
      saveDocuments(documents).pipeTo(sender())

    case GetDocuments =>
      getDocuments.pipeTo(sender())

    case GetDocumentsBySection(section) =>
      getDocumentsBySection(section).pipeTo(sender())

    case GetDocumentsByDocType(docType) =>
      getDocumentsByDocType(docType).pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }

  private def saveDocuments(documents: Documents): Future[Int] = {
    documentsDao.saveDocuments(documents.createAt, documents.section, documents.documentType, documents.subDocumentType)
  }

  private def getDocuments: Future[Seq[Documents]] = {
    documentsDao.getDocuments
  }

  private def getDocumentsBySection(section: Option[String] = None): Future[Seq[Documents]] = {
    documentsDao.getDocumentsBySection(section)
  }

  private def getDocumentsByDocType(docType: String): Future[Seq[Documents]] = {
    documentsDao.getDocumentsByDocType(docType)
  }

  private def create(data: Example): Future[Int] = {
    log.warning(s"daoga yuborildi: $data")
    exampleDao.create(data)
  }

  private def getList: Future[Seq[Example]] = {
    exampleDao.getAll
  }

  private def delete(id: Int): Future[Int] = {
    exampleDao.delete(id)
  }

  private def update(data: Example): Future[Int] = {
    exampleDao.update(data)
  }

}

