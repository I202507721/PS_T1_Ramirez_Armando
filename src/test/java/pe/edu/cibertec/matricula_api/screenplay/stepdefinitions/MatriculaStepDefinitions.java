package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatriculaStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private String token;
    private static long idMatriculaCreada;

    @Dado("que el alumno {string} con clave {string} inicia sesion en la API")
    public void iniciarSesion(String username, String password) {
        token = SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Cuando("el alumno {string} cotiza el curso {string}")
    public void cotizarUnCurso(String codigoAlumno, String codigoCurso) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "cursos", List.of(codigoCurso)
                ))
                .post("/api/matriculas/cotizacion");
    }

    @Cuando("el alumno {string} cotiza los cursos {string}")
    public void cotizarVariosCursos(String codigoAlumno, String cursosCsv) {
        var cursos = Arrays.stream(cursosCsv.split(","))
                .map(String::trim)
                .toList();

        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "cursos", cursos
                ))
                .post("/api/matriculas/cotizacion");
    }

    @Cuando("el alumno {string} se matricula en el curso {string}")
    public void matricularseEnCurso(String codigoAlumno, String codigoCurso) {
        idMatriculaCreada = SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "codigoCurso", codigoCurso
                ))
                .post("/api/matriculas")
                .jsonPath()
                .getLong("id");
    }

    @Cuando("el alumno paga la matricula registrada con metodo {string}")
    public void pagarMatricula(String metodo) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("metodo", metodo))
                .patch("/api/matriculas/{id}/pagar", idMatriculaCreada);
    }

    @Entonces("la respuesta tiene codigo HTTP {int}")
    public void verificarCodigoHttp(int status) {
        SerenityRest.lastResponse().then().statusCode(status);
    }

    @Y("el total de la cotizacion es {double}")
    public void verificarTotalCotizacion(double totalEsperado) {
        double totalActual = SerenityRest.lastResponse().jsonPath().getDouble("total");
        assertEquals(totalEsperado, totalActual, 0.01);
    }

    @Y("la matricula queda en estado {string} con monto {double} y esquema {string}")
    public void verificarMatriculaCreada(String estadoEsperado, double montoEsperado, String esquema) {
        SerenityRest.lastResponse().then()
                .body("estado", equalTo(estadoEsperado))
                .body(matchesJsonSchemaInClasspath("schemas/" + esquema));

        double montoActual = SerenityRest.lastResponse().jsonPath().getDouble("monto");
        assertEquals(montoEsperado, montoActual, 0.01);
    }

    @Y("la matricula queda en estado {string} con fecha de pago registrada")
    public void verificarMatriculaPagada(String estadoEsperado) {
        SerenityRest.lastResponse().then()
                .body("estado", equalTo(estadoEsperado))
                .body("fechaPago", notNullValue());
    }

    @Y("en la consulta de pagos existe el pago de la matricula con metodo {string} y formato de operacion valido")
    public void verificarPagoRegistrado(String metodoEsperado) {
        var json = SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .get("/api/pagos")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        String filtro = "find { it.idMatricula == " + idMatriculaCreada + " }";
        assertNotNull(json.get(filtro), "No se encontró el pago para idMatricula=" + idMatriculaCreada);
        assertEquals(metodoEsperado, json.getString(filtro + ".metodo"));

        String numeroOperacion = json.getString(filtro + ".numeroOperacion");
        assertNotNull(numeroOperacion);
        assertTrue(numeroOperacion.matches("^OP-\\d{6}$"),
                "Formato inválido de numeroOperacion: " + numeroOperacion);
    }

    @Y("el error tiene codigo {string} y cumple el esquema {string}")
    public void verificarError(String codigoEsperado, String esquema) {
        SerenityRest.lastResponse().then()
                .body("codigo", equalTo(codigoEsperado))
                .body(matchesJsonSchemaInClasspath("schemas/" + esquema));
    }
}