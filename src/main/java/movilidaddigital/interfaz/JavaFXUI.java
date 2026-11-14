package movilidaddigital.interfaz;

import movilidaddigital.aplicacion.MainApp;
import javafx.application.Application;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

public class JavaFXUI implements InterfazUsuario {
    private final GestorServicios gestorServicios;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;

    public JavaFXUI(GestorServicios gestorServicios, GestorUsuarios gestorUsuarios,
                    GestorViajes gestorViajes) {
        this.gestorServicios = gestorServicios;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @Override
    public void iniciar() {
        MainApp.configurar(gestorServicios, gestorUsuarios, gestorViajes);
        Application.launch(MainApp.class);
    }
}
