package movilidaddigital.interfaz;

import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

public class UIFactory {
    public static InterfazUsuario crearUI(String opcion, GestorServicios gestorServicios,
                                          GestorUsuarios gestorUsuarios, GestorViajes gestorViajes) {
        switch (opcion.trim().toUpperCase()) {
            case "A":
                return new ConsolaUI(gestorServicios, gestorUsuarios, gestorViajes);
            case "B":
                return new JavaFXUI();
            default:
                throw new IllegalArgumentException("Opción de interfaz no válida: " + opcion);
        }
    }
}
