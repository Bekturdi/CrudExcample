package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Environment, Mode}
import protocols.ExampleProtocol._
import scalaz.Scalaz.ToOptionIdOps
import views.html._

import java.util.Date
import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val webJarsUtil: WebJarsUtil,
                               @Named("example-manager") val exampleManager: ActorRef,
                               indexTemplate: index,
                               loginTemplate: login,
                               ws: WSClient,
                               environment: Environment
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)
  private val jsonV1 =
    """
        {
          "phoneNumber": "303-892-3965",
          "email": "mmaxwell@bandh.com",
          "company": "B&H Construction",
          "jobsiteAddress": "2682 Lyon Avenue, Hartland, WI",
          "siteContactName": "Mark Maxwell",
          "locationOfEquipment": "Behind Building"
        }
        """
  private val jsonV2 =
    """
        {
          "phoneNumber": "412-902-9642",
          "email": "CarlABenjamin@rhyta.com",
          "company": "Road & Highway Transport",
          "jobsiteAddress": "1861 Havanna Street, Winston Salem, NC",
          "siteContactName": "Carl Benjamin",
          "locationOfEquipment": "Equipment Lot"
        }
        """
  private val jsonV3 =
    """
        {
          "jobsiteAddress": "3274 Mesa Drive, Las Vegas",
          "locationOfEquipment": "Parking lot",
          "company": "Stage & Screen Lighting",
          "siteContactName": "Jeffrey Akin",
          "phoneNumber": "817-967-1777",
          "email": "JeffreyAkin@stagelight.com"
        }
        """
  private val jsonV4 =
    """
        {
          "phoneNumber": "845-308-3989",
          "email": "HMorales@futuredata.com",
          "company": "Future Data Corp",
          "jobsiteAddress": "4657 Ella Street, Palo Alto, CA",
          "siteContactName": "Howard Morales",
          "locationOfEquipment": "Utility area"
        }
        """

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

  def returnCorrespondingResponseByKey: Action[JsValue] = Action.async(parse.json) { implicit request => {
    try {
      (request.body \ "secretKey").asOpt[String] match {
        case Some(sk) if sk == "yei6heK3oo" =>
          val requestedData = (request.body \ "requestedData").as[String]
          val equipmentNumber = (request.body \ "equipment_number").asOpt[String]
          requestedData match {
            case "unatedRentalsInfo" =>
              if (equipmentNumber.exists(_.trim.nonEmpty)) {
                getUnitedRentalsInfoByEquipmentId(equipmentNumber.get)
              } else {
                Future.successful(Ok(Json.obj("error" -> "Couldn't get Customer info by Equipment ID")))
              }
          }
        case _ => Future.successful(Ok(Json.obj("error" -> "Invalid token")))
      }
    } catch {
      case e: Throwable =>
        logger.debug(s"Error response KEY: $e")
        Future.successful(BadRequest(s"$e"))
    }
  }
  }

  private def getUnitedRentalsInfoByEquipmentId(equipmentNumber: String): Future[Result] = {
    requestDellBoomi(equipmentNumber).map { res =>
      Ok(res)
    }
  }

  private def requestDellBoomi(equipmentNumber: String): Future[JsValue] = {
    val IsProdMode = environment.mode == Mode.Prod
    val url = if (IsProdMode) "https://dry-anchorage-45320.herokuapp.com/unitedrentals/dell-boomi/stub-api" else "http://localhost:9006/unitedrentals/dell-boomi/stub-api"
    val eqn = EquipmentNumber(equipmentNumber)
    ws.url(url)
      .post(Json.toJson(eqn))
      .map { res =>
        res.json
      }
  }

  def stubApiDellBoomi: Action[JsValue] = Action.async(parse.json) { implicit request =>
    try {
      val body = request.body
      val equipmentNumber = (body \ "number").as[String]
      logger.debug(s"equipmentNumber: $equipmentNumber")

      val result = equipmentNumber match {
        case "604661" => jsonV1
        case "1089992" => jsonV2
        case "10522733" => jsonV3
        case "102839344" => jsonV4
        case _ => jsonV1
      }
      Future.successful(Ok(result))
    } catch {
      case e: Throwable =>
        logger.error(s"Error stub api: $e")
        Future.successful(BadRequest(s"$e"))
    }
  }


}
