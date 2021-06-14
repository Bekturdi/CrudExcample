package dao

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.ExampleProtocol.{Documents, Example}
import slick.jdbc.JdbcProfile

import java.util.Date
import scala.concurrent.{ExecutionContext, Future}
import java.util.Date
import javax.inject.{Inject, Singleton}
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.MappedToBase.mappedToIsomorphism

import scala.concurrent.Future


trait ExampleComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class ExampleTable(tag: Tag) extends Table[Example](tag, "Example") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def tel = column[String]("tel")

    def age = column[String]("age")

    def address = column[String]("address")

    def * = (id.?, name, tel, age, address) <> (Example.tupled, Example.unapply _)
  }

  class DocumentsTable(tag: Tag) extends Table[Documents](tag, "document") with Date2SqlDate {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def createAt = column[Date]("createAt")
    def section = column[String]("section")
    def documentType = column[String]("documentType")
    def subDocumentType = column[String]("subDocumentType")

    def * = (id.?, createAt.?, section.?, documentType.?, subDocumentType.?) <> (Documents.tupled, Documents.unapply _)
  }
}

@ImplementedBy(classOf[ExampleDaoImpl])
trait ExampleDao {
  def create(data: Example): Future[Int]

  def saveDocuments(section: String, documentType: String, subDocumentType: String): Future[Int]

  def getAll: Future[Seq[Example]]

  def delete(id: Int): Future[Int]

  def update(data: Example): Future[Int]
}

@Singleton
class ExampleDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               val actorSystem: ActorSystem)
                              (implicit val ec: ExecutionContext)
  extends ExampleDao
    with ExampleComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with Date2SqlDate
    with LazyLogging {

  import utils.PostgresDriver.api._

  val examplesTable = TableQuery[ExampleTable]

  val documentTable = TableQuery[DocumentsTable]

  override def saveDocuments(section: String, documentType: String, subDocumentType: String): Future[Int] = {
    val data = Documents(section, documentType, subDocumentType)
    db.run {
      (documentTable returning documentTable.map(_.id)) += data
    }
  }

  override def create(data: Example): Future[Int] = {
    db.run {
      logger.warn(s"daoga keldi: $data")
      (examplesTable returning examplesTable.map(_.id)) += data
    }
  }

  override def getAll: Future[Seq[Example]] = {
    db.run {
      examplesTable.result
    }
  }

  override def delete(id: Int): Future[Int] = {
    db.run{
      examplesTable.filter(_.id === id).delete
    }
  }

  override def update(data: Example): Future[Int] = {
    db.run {
      examplesTable.filter(_.id === data.id).update(data)
    }
  }
}

