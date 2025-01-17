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
class ApunteControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "ApunteController" should {

    SharingNotes.resetearBD

    val admin = new Administrador()
    val usuario = new Usuario("María Jesús", "mjls130598@gmail.com", "MUII", "Granada")
    val PGPI_ID = admin.aniadirAsignatura("PGPI", "1º", "MUII", "Granada")
    val PGPI_T1 = admin.aniadirApunte("./documentos_prueba/Tema1_Definiciones.pdf",
    "Tema 1: Definiciones", SharingNotes.getAsignaturas(PGPI_ID))
    SharingNotes.aniadirUsuario(admin)
    SharingNotes.aniadirUsuario(usuario)

    val controller = new ApunteController(stubControllerComponents())

    "Comprueba que devuelve todos los apuntes" in {
      val home = controller.apuntes(None, None, None, None, None).apply(FakeRequest(GET, "/apuntes"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que devuelve los apuntes de una universidad" in {
      val home = controller.apuntes(Some("Granada"), None, None, None, None).apply(FakeRequest(GET, "/apuntes"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")

    }

    "Comprueba que devuelve todos los apuntes de una asignatura" in {
      val home = controller.apuntesAsignatura(PGPI_ID).apply(FakeRequest(GET, "/asignaturas"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que devuelve un error al consultar los apuntes de una asignatura" in {

      val home = controller.apuntesAsignatura("ASIG1234").apply(FakeRequest(GET, "/asignaturas"))

      status(home) mustBe NOT_FOUND
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que devuelve un apunte dado" in {
        
      val home = controller.apunte(PGPI_T1).apply(FakeRequest(GET, "/apuntes"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que devuelve un error al consultar un apunte que no existe" in {

      val home = controller.apunte("APUN1234").apply(FakeRequest(GET, "/apuntes"))

      status(home) mustBe NOT_FOUND
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario no registrado no puede guardar un apunte dado" in {
        
      val home = controller.addApunte().apply(FakeRequest(POST, "/apuntes").withJsonBody(
        Json.parse(s"""{"asignatura":"$PGPI_ID", "url":"./documentos_prueba/Intro_TID.pdf",
        "nombre":"Intro"}""")
      ))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que guarda un apunte dado" in {
        
      val home = controller.addApunte().apply(FakeRequest(POST, "/apuntes").withSession("usuario" -> usuario.correo)
      .withJsonBody(Json.parse(s"""{"asignatura":"$PGPI_ID", "url":"./documentos_prueba/Intro_TID.pdf",
        "nombre":"Intro"}""")))

      status(home) mustBe CREATED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario no registrado no borra un apunte" in {
      val home = controller.deleteApunte(PGPI_T1).apply(FakeRequest(DELETE, "/apuntes"))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que un usuario común no borra un apunte" in {
      val home = controller.deleteApunte(PGPI_T1).apply(FakeRequest(DELETE, "/apuntes").
      withSession("usuario" -> usuario.correo))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que se ha eliminado un apunte" in {
      val home = controller.deleteApunte(PGPI_T1).apply(FakeRequest(DELETE, "/apuntes").
      withSession("usuario" -> admin.correo))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "Comprueba que no se ha eliminado un apunte que no existe" in {
      val home = controller.deleteApunte("APUN1234").apply(FakeRequest(DELETE, "/apuntes").
      withSession("usuario" -> admin.correo))

      status(home) mustBe NOT_FOUND
      contentType(home) mustBe Some("application/json")
    }
  }
}