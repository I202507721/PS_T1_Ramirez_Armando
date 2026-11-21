# language: es
Característica: Seguridad de la sesión

  Escenario: Rotación del refresh token
    Dado que el usuario "Ana" se conecta a la API de seguridad
    Cuando inicia sesion con usuario "i202610001" y clave "Cibertec2026"
    Y respalda sus tokens iniciales en memoria
    Cuando renueva el token usando su refreshToken actual
    Entonces el estado HTTP de seguridad es 200
    Y obtiene tokens nuevos distintos a los iniciales
    Cuando intenta reutilizar el refreshToken original
    Entonces el estado HTTP de seguridad es 401
    Y el error de seguridad tiene codigo "REFRESH_REUTILIZADO" y cumple el esquema "error.json"
    Cuando consulta su perfil usando el accessToken nuevo
    Entonces el estado HTTP de seguridad es 401
    Y el error de seguridad tiene codigo "TOKEN_INVALIDO" y cumple el esquema "error.json"

  Escenario: Cierre de sesión
    Dado que el usuario "Ana" se conecta a la API de seguridad
    Cuando inicia sesion con usuario "i202610001" y clave "Cibertec2026"
    Cuando cierra su sesion activa
    Entonces el estado HTTP de seguridad es 204
    Cuando consulta su perfil con el mismo accessToken
    Entonces el estado HTTP de seguridad es 401
    Y el error de seguridad tiene codigo "TOKEN_INVALIDO" y cumple el esquema "error.json"

  Esquema del escenario: Bloqueo de cuenta por intentos fallidos
    Dado que el usuario "Jorge" se conecta a la API de seguridad
    Cuando inicia sesion con usuario "i202610004" y clave "<clave>"
    Entonces el estado HTTP de seguridad es <status>
    Y el error de seguridad tiene codigo "<codigo>" y cumple el esquema "error.json"

    Ejemplos:
      | intento | clave        | status | codigo                 |
      | 1       | MalaClave1   | 401    | CREDENCIALES_INVALIDAS |
      | 2       | MalaClave2   | 401    | CREDENCIALES_INVALIDAS |
      | 3       | MalaClave3   | 401    | CREDENCIALES_INVALIDAS |
      | 4       | MalaClave4   | 401    | CREDENCIALES_INVALIDAS |
      | 5       | MalaClave5   | 423    | CUENTA_BLOQUEADA       |
      | 6       | Cibertec2026 | 423    | CUENTA_BLOQUEADA       |

  Escenario: Alumno sin permiso de administrador
    Dado que el usuario "Ana" se conecta a la API de seguridad
    Cuando inicia sesion con usuario "i202610001" y clave "Cibertec2026"
    Cuando intenta listar todos los alumnos
    Entonces el estado HTTP de seguridad es 403
    Y el error de seguridad tiene codigo "ACCESO_DENEGADO" y cumple el esquema "error.json"