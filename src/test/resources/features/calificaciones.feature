# language: es
Característica: Calificación de matrículas

  Esquema del escenario: El docente registra la nota y el historial se actualiza
    Dado que el docente "D0001" y la alumna "i202610005" inician sesion con clave "Cibertec2026"
    Cuando la alumna "i202610005" consulta el id de su matricula del curso "PI5620"
    Y el docente "D0001" registra la nota <nota> en esa matricula
    Entonces el estado HTTP de calificacion es 200
    Cuando la alumna "i202610005" consulta su historial academico
    Entonces el estado HTTP de calificacion es 200
    Y el historial cumple el esquema "historial.json"
    Y en el historial el curso "PI5620" tiene nota <nota> y condicion "<condicion>"
    Y el historial muestra <creditos> creditos aprobados y promedio ponderado <promedio>

    Ejemplos:
      | nota | condicion   | creditos | promedio |
      | 11   | DESAPROBADO | 7        | 13,38    |
      | 12,5 | DESAPROBADO | 7        | 14,08    |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado que el docente "D0002" y la alumna "i202610005" inician sesion con clave "Cibertec2026"
    Cuando la alumna "i202610005" consulta el id de su matricula del curso "PI5620"
    Y el docente "D0002" registra la nota 15 en esa matricula
    Entonces el estado HTTP de calificacion es 403
    Y el error de calificacion tiene codigo "ACCESO_DENEGADO" y cumple el esquema "error.json"

  Escenario: No se puede calificar una matricula que no esta pagada
    Dado que el docente "D0001" y la alumna "i202610005" inician sesion con clave "Cibertec2026"
    Cuando la alumna "i202610005" consulta el id de su matricula del curso "PS5511"
    Y el docente "D0001" registra la nota 15 en esa matricula
    Entonces el estado HTTP de calificacion es 409
    Y el error de calificacion tiene codigo "MATRICULA_NO_PAGADA" y cumple el esquema "error.json"