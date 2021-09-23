package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Configuration, Environment}
import protocols.ExampleProtocol._
import scalaz.Scalaz.ToOptionIdOps
import views.html._

import java.util.Date
import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val webJarsUtil: WebJarsUtil,
                               @Named("example-manager") val exampleManager: ActorRef,
                               indexTemplate: index,
                               loginTemplate: login,
                               ws: WSClient,
                               environment: Environment,
                               val configuration: Configuration,
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def login: Action[AnyContent] = Action {
    Ok(loginTemplate())
  }

  def index: Action[AnyContent] = Action {
    Ok(indexTemplate())
  }

  def saveNewDocuments: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val section = (request.body \ "section").as[String]
    val documentType = (request.body \ "documentType").as[String]
    val subDocumentType = (request.body \ "subDocumentType").as[String]
    val group = (request.body \ "group").as[String]
    val executive = (request.body \ "executive").as[String]
    val data = Documents(createAt = new Date(), section = section, documentType = documentType,
      subDocumentType = subDocumentType, group = group.some, executive = executive.some)
    (exampleManager ? CmdDocuments(data)).mapTo[Int].map { id =>
      Ok(Json.toJson("Hujjat muvaffaqiyatli qo`shildi"))
    }.recover { case error =>
      logger.error("Hujjatni saqlashda xatolik yuz berdi. ", error)
      BadRequest("Hujjatni saqlashda xatolik yuz berdi. Iltimos qytadan urinib ko`ring.")
    }
  }

  def deleteDocuments(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[Int]
    (exampleManager ? DeleteDocuments(id)).mapTo[Int].map { i =>
      if (i != 0) {
        Ok(Json.toJson("Hujjat o`chirildi"))
      }
      else {
        Ok("Bunday Hujjat topilmadi")
      }
    }
  }

  def getDocuments: Action[AnyContent] = Action.async {
    (exampleManager ? GetDocuments).mapTo[Seq[Documents]].map { doc =>
      Ok(Json.toJson(doc))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    val tel = (request.body \ "tel").as[String]
    val age = (request.body \ "age").as[String]
    val address = (request.body \ "address").as[String]
    logger.warn(s"controllerga keldi")
    (exampleManager ? Create(Example(None, name, tel, age, address))).mapTo[Int].map { id =>
      Ok(Json.toJson(id))
    }
  }

  def getNames: Action[AnyContent] = Action.async {
    (exampleManager ? GetList).mapTo[Seq[Example]].map { name =>
      Ok(Json.toJson(name))
    }
  }

  def getDocumentsBySection(section: String): Action[AnyContent] = Action.async { implicit request =>
    (exampleManager ? GetDocumentsBySection(section.some)).mapTo[Seq[Documents]].map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getDocumentsByDocType(docType: String): Action[AnyContent] = Action.async { implicit request =>
    (exampleManager ? GetDocumentsByDocType(docType)).mapTo[Seq[Documents]].map { res =>
      Ok(Json.toJson(res))
    }
  }

  def delete = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[Int]
    (exampleManager ? Delete(id)).mapTo[Int].map { i =>
      if (i != 0) {
        Ok(Json.toJson(id + " raqamli ism o`chirildi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }

  def update = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[Int]
    val name = (request.body \ "name").as[String]
    val tel = (request.body \ "tel").as[String]
    val age = (request.body \ "age").as[String]
    val address = (request.body \ "address").as[String]
    (exampleManager ? Update(Example(Some(id), name, tel, age, address))).mapTo[Int].map { i =>
      if (i != 0) {
        Ok(Json.toJson(id + " raqamli ism yangilandi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }

}
