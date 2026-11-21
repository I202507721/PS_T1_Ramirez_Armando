package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarHistorial {

    public static Performable delAlumno(String codigoAlumno) {
        return Task.where("{0} consulta el historial academico de " + codigoAlumno, actor -> {
            String token = actor.recall("accessToken");
            actor.attemptsTo(
                    Get.resource("/api/alumnos/" + codigoAlumno + "/historial")
                            .with(req -> req.header("Authorization", "Bearer " + token))
            );
        });
    }
}