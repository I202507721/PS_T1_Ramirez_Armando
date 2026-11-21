package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula {

    public static Performable conNota(long idMatricula, double nota) {
        return Task.where("{0} registra la nota " + nota + " en la matricula " + idMatricula, actor -> {
            String token = actor.recall("accessToken");
            actor.attemptsTo(
                    Patch.to("/api/matriculas/" + idMatricula + "/nota")
                            .with(req -> req.header("Authorization", "Bearer " + token)
                                    .contentType(ContentType.JSON)
                                    .body(Map.of("nota", nota)))
            );
        });
    }
}