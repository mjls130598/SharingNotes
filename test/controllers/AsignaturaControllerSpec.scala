package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

import models.SharingNotes._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class AsignaturaControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "AsignaturaController" should {

    SharingNotes.resetearBD

    val admin = new Administrador()
    val usuario = new Usuario("María Jesús", "mjls130598@gmail.com", "MUII", "Granada")
    SharingNotes.aniadirUsuario(admin)
    SharingNotes.aniadirUsuario(usuario)

    val controller = new AsignaturaController(stubControllerComponents())

    "Comprueba que guarda una asignatura dada" in {
        
      val home = controller.addAsignatura().apply(FakeRequest(POST, "/asignaturas")
      .withSession("usuario" -> admin.correo).withJsonBody(
      Json.parse(s"""{"nombre":"CC", "universidad":"Universidad de Granada",
      "carrera":"MUII", "curso": "1º"}""")
      ))

      status(home) mustBe CREATED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario común no añade una asignatura" in {

      val home = controller.addAsignatura().apply(FakeRequest(POST, "/asignaturas").
      withSession("usuario" -> usuario.correo).withJsonBody(
      Json.parse(s"""{"nombre":"CC","universidad":"Universidad de Granada", "carrera":"MUII",
      "curso": "1º"}""")
      ))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario no registrado no añade una asignatura" in {

      val home = controller.addAsignatura().apply(FakeRequest(POST, "/asignaturas").withJsonBody(
      Json.parse(s"""{"nombre":"CC","universidad":"Universidad de Granada", "carrera":"MUII",
      "curso": "1º"}""")
      ))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario no registrado no borra una asignatura" in {
      val home = controller.deleteAsignatura(SharingNotes.getAsignaturas.last._1).
      apply(FakeRequest(DELETE, "/asignaturas"))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario común no borra una asignatura" in {
      val home = controller.deleteAsignatura(SharingNotes.getAsignaturas.last._1).
      apply(FakeRequest(DELETE, "/asignaturas").withSession("usuario" -> usuario.correo))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que se ha eliminado una asignatura" in {
      val home = controller.deleteAsignatura(SharingNotes.getAsignaturas.last._1).
      apply(FakeRequest(DELETE, "/asignaturas").withSession("usuario" -> admin.correo))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que no se ha eliminado una asignatura que no existe" in {
      val home = controller.deleteAsignatura("ASIG1234").
      apply(FakeRequest(DELETE, "/asignaturas").withSession("usuario" -> admin.correo))

      status(home) mustBe NOT_FOUND
      contentType(home) mustBe Some("application/json")
    }

  }
}