package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class CerrarSesion {

    public static Performable conSuToken() {
        return Task.where("{0} cierra su sesion activa", actor -> {
            String token = actor.recall("accessToken");
            actor.attemptsTo(
                    Post.to("/api/auth/logout")
                            .with(req -> req.header("Authorization", "Bearer " + token))
            );
        });
    }
}