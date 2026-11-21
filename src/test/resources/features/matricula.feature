# language: es
Característica: Matrícula del alumno

  Antecedentes:
    Dado que el alumno "i202610001" con clave "Cibertec2026" inicia sesion en la API

  Escenario: Cotizar el curso de tu fila
    Cuando el alumno "i202610001" cotiza el curso "PI5620"
    Entonces la respuesta tiene codigo HTTP 200
    Y el total de la cotizacion es 576,0

  Escenario: Matricularse y pagar el curso de tu fila
    Cuando el alumno "i202610001" se matricula en el curso "PI5620"
    Entonces la respuesta tiene codigo HTTP 201
    Y la matricula queda en estado "REGISTRADA" con monto 576,0 y esquema "matricula.json"
    Cuando el alumno paga la matricula registrada con metodo "TARJETA"
    Entonces la respuesta tiene codigo HTTP 200
    Y la matricula queda en estado "PAGADA" con fecha de pago registrada
    Y en la consulta de pagos existe el pago de la matricula con metodo "TARJETA" y formato de operacion valido

  Escenario: Regla de negocio que debe fallar
    Cuando el alumno "i202610001" cotiza los cursos "PS5511, QA5517, DM5512, TS5514, SA5516, BD5410"
    Entonces la respuesta tiene codigo HTTP 422
    Y el error tiene codigo "LIMITE_CREDITOS" y cumple el esquema "error.json"