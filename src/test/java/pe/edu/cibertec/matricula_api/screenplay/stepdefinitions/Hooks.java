package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @Before(order = 0)
    public void beforeScenario() {
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Estudiante : Armando Ramírez Prada");
        System.out.println("Código     : I202507721 (D = 1)");
        System.out.println("Fecha/Hora : " + fechaHora);

        OnStage.setTheStage(new OnlineCast());
    }
}