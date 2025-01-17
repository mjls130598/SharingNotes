import models.SharingNotes._
import org.scalatest.FunSuite
import java.io.File

class SharingNotesTest extends FunSuite {

  SharingNotes.resetearBD

  val usuario = new Usuario("María Jesús", "mjls130598@gmail.com", "MUII", "Granada")
  val admin = new Administrador()

  // Comprueba que se ha insertado los dos usuarios anteriores al sistema

  SharingNotes.aniadirUsuario(usuario)
  SharingNotes.aniadirUsuario(admin)

  test("Nuevos usuarios") {
    assertResult(2){
      SharingNotes.getUsuarios.size
    }
    info("El nuevo usuario se ha insertado correctamente")
  }

  // Comprueba que no se añaden dos veces un mismo correo en el sistema

  test("Tercer usuario con la misma cuenta de correo que el primero no se guarda"){
    assertThrows[java.lang.Exception]{
      SharingNotes.aniadirUsuario(usuario)
    }
  }

  // Comprueba que no se añade una dirección de correo correcta

  test("Usuario con dirección de correo errónea no guardado"){
    assertThrows[java.lang.Exception]{
      val usuario2 = new Usuario("María Jesús", "mjls1 30598 @gmail.com", "MUII", "Granada")
      SharingNotes.aniadirUsuario(usuario2)
    }
  }

  // Comprueba que se ha añadido una asignatura correctamente

  val PGPI_ID = admin.aniadirAsignatura("PGPI", "1º", "MUII", "Granada")

  test("Nueva asignatura"){
    assertResult(1){
      SharingNotes.getAsignaturas.size
    }
    info("La nueva asignatura se ha insertado correctamente")
  }

  // Comprueba que se ha añadido un nuevo apunte

  val PGPI_T1 = usuario.aniadirApunte("./documentos_prueba/Tema1_Definiciones.pdf",
    "Tema 1: Definiciones", SharingNotes.getAsignaturas(PGPI_ID))

  test("Nuevo apunte"){
    assertResult(1){
      SharingNotes.getApuntes.size
    }
    info("El nuevo apunte se ha insertado correctamente")
  }

  // Comprueba que se ha añadido el apunte correcto

  test("Insertar apunte con un formato distinto a PDF"){
    assertThrows[java.lang.Exception]{
      usuario.aniadirApunte("./documentos_prueba/plantilla.tex",
        "Práctica 1", SharingNotes.getAsignaturas(PGPI_ID))
    }
    info("No se ha insertado, sólo se añaden PDFs")
  }

  // Comprueba que se ha añadido un apunte en una asignatura que existe

  test("No insertar apunte de una asignatura que no existe"){
    assertThrows[java.lang.Exception]{
      usuario.aniadirApunte("./documentos_prueba/Tema1_Definiciones.pdf",
        "Tema 1: Definiciones", new Asignatura("1234", "Una asignatura", "1º", "Ingeniería", "Granada"))
    }
    info("No se ha insertado, sólo se añaden PDFs")
  }

  // Comprueba que se ha añadido un comentario correctamente

  val PGPIT1_C1 = usuario.aniadirComentario("Esto es un comentario cualquiera", SharingNotes.getApuntes(PGPI_T1))

  test("Nuevo comentario"){
    assertResult(1){
      SharingNotes.getComentarios.size
    }
    assert(SharingNotes.getComentarios.values.exists(_.apunte == SharingNotes.getApuntes(PGPI_T1)))
    info("El nuevo comentario se ha insertado correctamente")
  }

  // Comprueba que el comentario sobre un apunte no existente no funciona

  test("Nuevo comentario sobre un apunte desconocido"){
    assertThrows[java.lang.Exception]{
      SharingNotes.aniadirComentario("Esto es un comentario cualquiera",
        new Apunte ("1234", "aqui", "Algún apunte", SharingNotes.getAsignaturas(PGPI_ID), usuario),
        usuario)
    }
  }

  // Comprueba que se ha borrado el comentario anteriormente insertado correctamente

  val PGPIT1_C2 = usuario.aniadirComentario("Esto es el tercer comentario", SharingNotes.getApuntes(PGPI_T1))

  val comentarioBorrado = admin.borrarComentario(PGPIT1_C2)

  test("Borrar un comentario"){
    assert(comentarioBorrado)
    assert(!SharingNotes.getComentarios.keys.exists(_ == PGPIT1_C2))
    info("El comentario se ha borrado correctamente")
  }

  // Comprueba que se encuentra los comentarios de un apunte

  test("Búsqueda de los comentarios de un apunte"){
    assertResult(1){
      usuario.buscarComentarios(SharingNotes.getApuntes(PGPI_T1)).size
    }
  }

  // Comprueba que se eliminan los apuntes correctamente

  val PGPI_T2 = usuario.aniadirApunte("./documentos_prueba/Tema2_Preparacióndeproyectos.pdf",
    "Tema 2: Preparación de proyectos", SharingNotes.getAsignaturas(PGPI_ID))

  val apunteBorrado = admin.borrarApunte(PGPI_T2)

  test("Borrar un apunte"){
    assert(apunteBorrado)
    assert(!SharingNotes.getApuntes.keys.exists(_ == PGPI_T2))
    assert(!new File("./documentos/" + PGPI_ID + "/Tema2_Preparacióndeproyectos.pdf").exists)
    info("El apunte se ha borrado correctamente")
  }

  // Comprueba que se eliminan todos los comentarios sobre un apunte

  val PGPI_T2_2 = usuario.aniadirApunte("./documentos_prueba/Tema2_Preparacióndeproyectos.pdf",
    "Tema 2: Preparación de proyectos", SharingNotes.getAsignaturas(PGPI_ID))

  val PGPIT2_C1 = usuario.aniadirComentario("Este es el cuarto comentario realizado", SharingNotes.getApuntes(PGPI_T2_2))

  admin.borrarApunte(PGPI_T2_2)

  test("Borrar apunte y sus correspondientes comentarios"){
    assert(!SharingNotes.getApuntes.keys.exists(_ == PGPI_T2_2))
    assert(!SharingNotes.getComentarios.values.exists(_.apunte.identificador == PGPI_T2_2))
    info("No está en el sistema el apunte ni los comentarios realizados sobre él")
  }

  // Comprueba que se encuentran los apuntes de una asignatura

  test("Búsqueda de los apuntes de una asignatura"){
    assertResult(1){
      usuario.buscarApuntes(SharingNotes.getAsignaturas(PGPI_ID)).size
    }
  }

  // Comprueba que se ha eliminado una asignatura correctamente

  val TID_ID = admin.aniadirAsignatura("TID", "1º", "MUII", "Granada")

  val asignaturaBorrada = admin.borrarAsignatura(TID_ID)

  test("Asignatura borrada correctamente"){
    assert(asignaturaBorrada)
    assert(!SharingNotes.getAsignaturas.keys.exists(_ == TID_ID))
    assert(!new File("./documentos/" + TID_ID).exists)
    info("La asignatura no se encuentra almacenada en el sistema")
  }

  // Comprueba que se ha eliminado los apuntes de una asignatura cuando
  // se elimina ésta

  val TID_ID2 = admin.aniadirAsignatura("TID", "1º", "MUII", "Granada")

  val TID_T1 = usuario.aniadirApunte("./documentos_prueba/Intro_TID.pdf",
    "Tema 1 Introducción", SharingNotes.getAsignaturas(TID_ID2))
  val TID_T2 = usuario.aniadirApunte("./documentos_prueba/PreparacionDatos.pdf",
    "Tema 2 Preparación de datos", SharingNotes.getAsignaturas(TID_ID2))

  val TIDT1_C1 = usuario.aniadirComentario("Este es el cuarto comentario realizado",
    SharingNotes.getApuntes(TID_T1))

  admin.borrarAsignatura(TID_ID2)

  test("Asignatura, apuntes y comentarios borrados correctamente"){
    assert(!SharingNotes.getAsignaturas.keys.exists(_ == TID_ID2))
    assert(!SharingNotes.getApuntes.values.exists(_.asignatura.identificador == TID_ID2))
    assert(!SharingNotes.getComentarios.values.exists(_.apunte.asignatura.identificador == TID_ID2))
    info("No se encuentra la asignatura, ni los apuntes sobre una asignatura ni " +
      "los comentarios realizados sobre cada un de los apuntes anteriores en el sistema")
  }
}