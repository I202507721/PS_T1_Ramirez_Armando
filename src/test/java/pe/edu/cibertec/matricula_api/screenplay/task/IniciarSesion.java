package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class IniciarSesion {

    public static Performable conCredenciales(String username, String password) {
        return Task.where("{0} inicia sesion con usuario #username",
                Post.to("/api/auth/login")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("username", username, "password", password))),
                Task.where("{0} guarda sus tokens en memoria", actor -> {
                    if (SerenityRest.lastResponse().statusCode() == 200) {
                        String access = SerenityRest.lastResponse().jsonPath().getString("accessToken");
                        String refresh = SerenityRest.lastResponse().jsonPath().getString("refreshToken");
                        actor.remember("accessToken", access);
                        actor.remember("refreshToken", refresh);
                    }
                })
        );
    }
}