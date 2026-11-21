package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class RenovarToken {

    public static Performable usandoRefreshToken(String refreshToken) {
        return Task.where("{0} renueva su token de sesion",
                Post.to("/api/auth/refresh")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refreshToken))),
                Task.where("{0} actualiza sus tokens si la renovacion fue exitosa", actor -> {
                    if (SerenityRest.lastResponse().statusCode() == 200) {
                        String nuevoAccess = SerenityRest.lastResponse().jsonPath().getString("accessToken");
                        String nuevoRefresh = SerenityRest.lastResponse().jsonPath().getString("refreshToken");
                        actor.remember("accessToken", nuevoAccess);
                        actor.remember("refreshToken", nuevoRefresh);
                    }
                })
        );
    }
}