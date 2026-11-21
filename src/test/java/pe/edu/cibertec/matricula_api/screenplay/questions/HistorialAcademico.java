package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class HistorialAcademico {

    public static Question creditosAprobados() {
        return Question.about("los creditos aprobados en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getInt("creditosAprobados"));
    }

    public static Question promedioPonderado() {
        return Question.about("el promedio ponderado en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getDouble("promedioPonderado"));
    }

    public static Question condicionDelCurso(String codigoCurso) {
        return Question.about("la condicion del curso " + codigoCurso + " en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getString("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.condicion"));
    }

    public static Question notaDelCurso(String codigoCurso) {
        return Question.about("la nota del curso " + codigoCurso + " en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getDouble("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.nota"));
    }
}