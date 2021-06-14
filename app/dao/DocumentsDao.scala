package dao
import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.ExampleProtocol.{Documents, Example}
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


trait DocumentsComponent {
	self: HasDatabaseConfigProvider[JdbcProfile] =>

	import utils.PostgresDriver.api._

	class DocumentsTable(tag: Tag) extends Table[Documents](tag, "documents") with Date2SqlDate {
		def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

		def createAt = column[Date]("createAt")

		def section = column[String]("section")

		def documentType = column[String]("documentType")

		def subDocumentType = column[String]("subDocumentType")

		def * = (id.?, createAt, section, documentType, subDocumentType) <> (Documents.tupled, Documents.unapply _)
	}
}

@ImplementedBy(classOf[DocumentsDaoImpl])
trait DocumentsDao {
		def saveDocuments(createAt: Date, section: String, documentType: String, subDocumentType: String): Future[Int]

		def getDocuments: Future[Seq[Documents]]
	}

	@Singleton
	class DocumentsDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
	                                 val actorSystem: ActorSystem)
	                                (implicit val ec: ExecutionContext)
		extends DocumentsDao
			with DocumentsComponent
			with HasDatabaseConfigProvider[JdbcProfile]
			with Date2SqlDate
			with LazyLogging {

		import utils.PostgresDriver.api._

		val documentTable = TableQuery[DocumentsTable]

		override def saveDocuments(createAt: Date, section: String, documentType: String, subDocumentType: String): Future[Int] = {
			val data = Documents(createAt = createAt, section = section, documentType = documentType, subDocumentType = subDocumentType)
			db.run {
				(documentTable returning documentTable.map(_.id)) += data
			}
		}

		override def getDocuments: Future[Seq[Documents]] = {
			db.run {
				documentTable.result
			}
		}
	}


