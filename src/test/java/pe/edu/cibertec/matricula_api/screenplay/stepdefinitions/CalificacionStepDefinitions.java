package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.CodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.HistorialAcademico;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalificacionStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private long idMatriculaObjetivo;

    @Dado("que el docente {string} y la alumna {string} inician sesion con clave {string}")
    public void iniciarSesionDocenteYAlumna(String codigoDocente, String codigoAlumna, String clave) {
        var alumna = theActorCalled(codigoAlumna);
        alumna.whoCan(CallAnApi.at(baseUrl));
        alumna.attemptsTo(IniciarSesion.conCredenciales(codigoAlumna, clave));

        var docente = theActorCalled(codigoDocente);
        docente.whoCan(CallAnApi.at(baseUrl));
        docente.attemptsTo(IniciarSesion.conCredenciales(codigoDocente, clave));
    }

    @Cuando("la alumna {string} consulta el id de su matricula del curso {string}")
    public void consultarIdMatricula(String codigoAlumna, String codigoCurso) {
        var alumna = theActorCalled(codigoAlumna);
        String token = alumna.recall("accessToken");

        alumna.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );

        idMatriculaObjetivo = SerenityRest.lastResponse().jsonPath()
                .getLong("find { (it.codigoCurso == '" + codigoCurso
                        + "' || it.curso == '" + codigoCurso
                        + "' || it.curso?.codigo == '" + codigoCurso + "') }.id");
    }

    @Y("el docente {string} registra la nota {double} en esa matricula")
    public void registrarNotaEnMatricula(String codigoDocente, double nota) {
        theActorCalled(codigoDocente).attemptsTo(
                CalificarMatricula.conNota(idMatriculaObjetivo, nota)
        );
    }

    @Cuando("la alumna {string} consulta su historial academico")
    public void consultarHistorialAlumna(String codigoAlumna) {
        theActorCalled(codigoAlumna).attemptsTo(
                ConsultarHistorial.delAlumno(codigoAlumna)
        );
    }

    @Entonces("el estado HTTP de calificacion es {int}")
    public void verificarEstadoHttpCalificacion(int status) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(status))
        );
    }

    @Y("el historial cumple el esquema {string}")
    public void verificarEsquemaHistorial(String esquema) {
        theActorInTheSpotlight().should(
                seeThatResponse("el historial cumple " + esquema,
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/" + esquema)))
        );
    }

    @Y("en el historial el curso {string} tiene nota {double} y condicion {string}")
    public void verificarCursoEnHistorial(String codigoCurso, double notaEsperada, String condicionEsperada) {
        var actor = theActorInTheSpotlight();
        actor.should(
                seeThat(HistorialAcademico.condicionDelCurso(codigoCurso), equalTo(condicionEsperada))
        );
        double notaActual = ((Number) HistorialAcademico.notaDelCurso(codigoCurso).answeredBy(actor)).doubleValue();
        assertEquals(notaEsperada, notaActual, 0.01);
    }

    @Y("el historial muestra {int} creditos aprobados y promedio ponderado {double}")
    public void verificarTotalesHistorial(int creditosEsperados, double promedioEsperado) {
        var actor = theActorInTheSpotlight();
        actor.should(
                seeThat(HistorialAcademico.creditosAprobados(), equalTo(creditosEsperados))
        );
        double promedioActual = ((Number) HistorialAcademico.promedioPonderado().answeredBy(actor)).doubleValue();
        assertEquals(promedioEsperado, promedioActual, 0.01);
    }

    @Y("el error de calificacion tiene codigo {string} y cumple el esquema {string}")
    public void verificarErrorCalificacion(String codigoEsperado, String esquema) {
        theActorInTheSpotlight().should(
                seeThat(CodigoDeError.obtenido(), equalTo(codigoEsperado)),
                seeThatResponse("cumple el esquema " + esquema,
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/" + esquema)))
        );
    }
}