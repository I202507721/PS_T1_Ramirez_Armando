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
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class SeguridadStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Dado("que el usuario {string} se conecta a la API de seguridad")
    public void conectarUsuario(String nombre) {
        theActorCalled(nombre).whoCan(CallAnApi.at(baseUrl));
    }

    @Cuando("inicia sesion con usuario {string} y clave {string}")
    public void iniciarSesionUsuario(String username, String password) {
        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.conCredenciales(username, password)
        );
    }

    @Y("respalda sus tokens iniciales en memoria")
    public void respaldarTokensIniciales() {
        var actor = theActorInTheSpotlight();
        String accessInicial = actor.recall("accessToken");
        String refreshInicial = actor.recall("refreshToken");
        actor.remember("accessTokenInicial", accessInicial);
        actor.remember("refreshTokenInicial", refreshInicial);
    }

    @Cuando("renueva el token usando su refreshToken actual")
    public void renovarConRefreshActual() {
        var actor = theActorInTheSpotlight();
        String refreshActual = actor.recall("refreshToken");
        actor.attemptsTo(RenovarToken.usandoRefreshToken(refreshActual));
    }

    @Cuando("intenta reutilizar el refreshToken original")
    public void reutilizarRefreshOriginal() {
        var actor = theActorInTheSpotlight();
        String refreshOriginal = actor.recall("refreshTokenInicial");
        actor.attemptsTo(RenovarToken.usandoRefreshToken(refreshOriginal));
    }

    @Cuando("consulta su perfil usando el accessToken nuevo")
    public void consultarPerfilConTokenNuevo() {
        var actor = theActorInTheSpotlight();
        String accessNuevo = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header("Authorization", "Bearer " + accessNuevo))
        );
    }

    @Cuando("cierra su sesion activa")
    public void cerrarSesionActiva() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.conSuToken());
    }

    @Cuando("consulta su perfil con el mismo accessToken")
    public void consultarPerfilMismoToken() {
        var actor = theActorInTheSpotlight();
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }

    @Cuando("intenta listar todos los alumnos")
    public void intentarListarAlumnos() {
        var actor = theActorInTheSpotlight();
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/alumnos")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }

    @Entonces("el estado HTTP de seguridad es {int}")
    public void verificarEstadoHttp(int status) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(status))
        );
    }

    @Y("obtiene tokens nuevos distintos a los iniciales")
    public void verificarTokensNuevos() {
        var actor = theActorInTheSpotlight();
        String accessInicial = actor.recall("accessTokenInicial");
        String refreshInicial = actor.recall("refreshTokenInicial");

        actor.should(
                seeThatResponse("los tokens nuevos son validos y distintos",
                        res -> res.body("accessToken", notNullValue())
                                .body("refreshToken", notNullValue())
                                .body("accessToken", not(equalTo(accessInicial)))
                                .body("refreshToken", not(equalTo(refreshInicial)))
                )
        );
    }

    @Y("el error de seguridad tiene codigo {string} y cumple el esquema {string}")
    public void verificarErrorSeguridad(String codigoEsperado, String esquema) {
        theActorInTheSpotlight().should(
                seeThat(CodigoDeError.obtenido(), equalTo(codigoEsperado)),
                seeThatResponse("cumple el esquema " + esquema,
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/" + esquema)))
        );
    }
}