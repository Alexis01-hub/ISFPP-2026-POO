package movilidaddigital.aplicacion;

import movilidaddigital.datos.CargadorDatos;
import movilidaddigital.datos.CargadorDatosBD;
import movilidaddigital.interfaz.InterfazUsuario;
import movilidaddigital.interfaz.UIFactory;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;
import movilidaddigital.datastructures.Map;

import java.util.Scanner;

/** Punto de entrada: prepara el dominio y delega la interacción a la UI elegida. */
public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Seleccione el origen de datos:");
        System.out.println("[A] Archivos TXT");
        System.out.println("[B] Base de datos PostgreSQL");
        System.out.print("Opción: ");
        String origen = entrada.nextLine().trim().toUpperCase();

        GestorServicios gestorServicios;
        GestorUsuarios gestorUsuarios;
        if ("A".equals(origen)) {
            CargadorDatos cargador = new CargadorDatos();
            gestorServicios = new GestorServicios(cargador.cargarServicios());
            Map<String, Vehiculo> vehiculos = cargador.cargarVehiculos();
            gestorUsuarios = new GestorUsuarios(cargador.cargarUsuarios(vehiculos));
            for (String aviso : cargador.getAdvertencias()) {
                System.out.println("AVISO: " + aviso);
            }
        } else if ("B".equals(origen)) {
            CargadorDatosBD cargador = new CargadorDatosBD();
            gestorServicios = new GestorServicios(cargador.cargarServicios());
            Map<String, Vehiculo> vehiculos = cargador.cargarVehiculos();
            gestorUsuarios = new GestorUsuarios(cargador.cargarUsuarios(vehiculos));
        } else {
            System.err.println("Origen de datos no válido.");
            return;
        }
        GestorViajes gestorViajes = new GestorViajes(gestorServicios,
                new Despachador(gestorUsuarios));

        System.out.println("Seleccione el modo de ejecución:");
        System.out.println("[A] Modo Consola");
        System.out.println("[B] Modo Gráfico (JavaFX)");
        System.out.print("Opción: ");
        String opcion = entrada.nextLine();

        try {
            InterfazUsuario ui = UIFactory.crearUI(opcion, gestorServicios,
                    gestorUsuarios, gestorViajes);
            ui.iniciar();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
