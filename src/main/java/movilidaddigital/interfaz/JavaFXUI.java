package movilidaddigital.interfaz;

import movilidaddigital.aplicacion.MainApp;
import javafx.application.Application;

public class JavaFXUI implements InterfazUsuario {
    @Override
    public void iniciar() {
        // Application.launch inicializa el entorno JavaFX y llama al método start() de MainApp
        Application.launch(MainApp.class);
    }

}
