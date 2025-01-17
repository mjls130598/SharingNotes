package models.SharingNotes

object Principal{

  def main(args: Array[String]): Unit = {

    // Añadir usuario a la memoria del proyecto

    val usuario = new Usuario("María Jesús", "mjls130598@gmail.com", "MUII", "Granada")
    val admin = new Administrador()

    SharingNotes.aniadirUsuario(usuario)
    SharingNotes.aniadirUsuario(admin)

    println("\n/*****************************************************/\n")

    println("Usuarios del sistema:")

    SharingNotes.getUsuarios.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.nombre)
    }

    println("\n/*****************************************************/\n")

    // Añadir asignatura a la memoria del proyecto

    val PGPI_ID = admin.aniadirAsignatura("PGPI", "1º", "MUII", "Granada")

    println("Asignaturas del sistema:")

    SharingNotes.getAsignaturas.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.nombre)
    }

    println("\n/*****************************************************/\n")

    // Añadir apunte a la memoria del proyecto

    println("Apuntes de la asignatura de PGPI:")

    val PGPI_T1 = usuario.aniadirApunte("./documentos_prueba/Tema1_Definiciones.pdf",
      "Tema 1: Definiciones", SharingNotes.getAsignaturas(PGPI_ID))

    SharingNotes.getApuntes.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.nombre)
    }

    println("\n/*****************************************************/\n")

    // Añadir comentario a la memoria del proyecto

    println("Comentarios guardados:")

    val PGPIT1_C1 = usuario.aniadirComentario("Esto es un comentario cualquiera", SharingNotes.getApuntes(PGPI_T1))
    SharingNotes.getComentarios.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.comentario)
    }

    println("\n/*****************************************************/\n")

    // Borrar comentario de la memoria del proyecto

    println("Número de comentarios sobre Tema 1: Definiciones después de borrar el último:")

    admin.borrarComentario(PGPIT1_C1)
    println("\t" + SharingNotes.getComentarios.size)

    println("\n/*****************************************************/\n")

    // Buscar comentarios de un apunte

    val PGPIT1_C2 = usuario.aniadirComentario("Esto es un comentario cualquiera", SharingNotes.getApuntes(PGPI_T1))
    val PGPIT1_C3 = usuario.aniadirComentario("Esto es otro comentario cualquiera", SharingNotes.getApuntes(PGPI_T1))

    println("Comentarios de Tema 1: Definiciones:")

    val comentarios = SharingNotes.buscarComentarios(SharingNotes.getApuntes(PGPI_T1))
    comentarios.foreach{
      case (coment) => println ("\t" + coment.identificador + " -> " + coment.comentario)
    }

    println("\n/*****************************************************/\n")

    // Borrar apunte de la memoria del proyecto

    println("Comentarios resultantes después de borrar Tema 1: Definiciones:")

    admin.borrarApunte(PGPI_T1)
    SharingNotes.getComentarios.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.comentario)
    }

    println("\n/*****************************************************/\n")

    // Buscar apuntes de una asignatura

    println("Apuntes de la asignatura PGPI:")

    usuario.aniadirApunte("./documentos_prueba/Tema1_Definiciones.pdf",
      "Tema 1: Definiciones", SharingNotes.getAsignaturas(PGPI_ID))
    usuario.aniadirApunte("./documentos_prueba/Tema2_Preparacióndeproyectos.pdf",
      "Tema 2: Preparación de proyectos", SharingNotes.getAsignaturas(PGPI_ID))

    val apuntes = SharingNotes.buscarApuntes(SharingNotes.getAsignaturas(PGPI_ID))
    apuntes.foreach{
      case (apunte) => println("\t" + apunte.identificador + " -> " + apunte.nombre)
    }

    println("\n/*****************************************************/\n")

    // Borrar una asignatura de la memoria del proyecto

    admin.borrarAsignatura(PGPI_ID)

    println("Apuntes después de borrar la asignatura PGPI:")

    SharingNotes.getApuntes.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.identificador)
    }

    println("\n/*****************************************************/\n")

    println("Comentarios después de borrar los apuntes de la asignatura PGPI:")
    SharingNotes.getComentarios.foreach{
      case (key, value) => println ("\t" + key + " -> " + value.comentario)
    }

    println("\n/*****************************************************/\n")
  }
}
