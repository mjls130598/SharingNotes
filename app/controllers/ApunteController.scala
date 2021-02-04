package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import models.SharingNotes._

@Singleton
class ApunteController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  
  def apuntes(universidad: Option[String], carrera: Option[String],
    asignatura: Option[String], apunte: Option[String], curso: Option[String]) = Action { implicit request: Request[AnyContent] =>
    
    if(carrera == None && universidad == None && asignatura == None && apunte == None && curso == None){

      val apuntes = SharingNotes.getApuntes
      Ok(Json.toJson(apuntes.values.toList))
    }

    else
      Ok("Aún nada")
  }

  def apuntesAsignatura (id: String) = Action { implicit request: Request[AnyContent] =>
  
    val apuntes = SharingNotes.buscarApuntes(SharingNotes.getAsignaturas(id))

    if(apuntes.nonEmpty)
      Ok(Json.toJson(apuntes))
    
    else
      NotFound
  }

}
