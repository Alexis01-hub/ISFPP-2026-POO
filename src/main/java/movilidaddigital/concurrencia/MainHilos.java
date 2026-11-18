package movilidaddigital.concurrencia;

import movilidaddigital.datos.CargadorDatos;
import movilidaddigital.datastructures.Map;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.RolUsuario;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Punto de entrada independiente para la práctica de hilos.
 * No inicia JavaFX ni carga datos de la aplicación principal.
 */
public final class MainHilos {
    private MainHilos() {
    }

    public static void main(String[] args) throws InterruptedException {
        CargadorDatos cargador = new CargadorDatos();
        GestorServicios gestorServicios = new GestorServicios(cargador.cargarServicios());
        Map<String, Vehiculo> vehiculos = cargador.cargarVehiculos();
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cargador.cargarUsuarios(vehiculos));
        GestorViajes gestorViajes = new GestorViajes(gestorServicios,
                new Despachador(gestorUsuarios));
        int cantidadClientes = leerCantidadClientes();

        List<Usuario> clientes = new ArrayList<>();
        List<Usuario> conductores = new ArrayList<>();
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario.getConductor() == null) {
                clientes.add(usuario);
            }
            if (usuario.getConductor() != null) {
                conductores.add(usuario);
            }
        }
        if (clientes.size() < cantidadClientes || conductores.size() < 2
                || gestorServicios.getServicios().isEmpty()) {
            throw new IllegalStateException("Los TXT no tienen suficientes clientes, dos conductores o servicio.");
        }

        var despachador = new Despachador(gestorUsuarios);
        var servicio = gestorServicios.getServicios().stream()
                .filter(s -> cantidadCompatibles(conductores, despachador, s) >= 2)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay un servicio compatible con al menos dos conductores."));
        List<Usuario> conductoresCompetidores = new ArrayList<>();
        for (Usuario conductor : conductores) {
            if (despachador.esCompatible(conductor.getConductor(), servicio)) {
                conductor.cambiarRolActivo(RolUsuario.CONDUCTOR);
                gestorUsuarios.ponerDisponible(conductor);
                conductoresCompetidores.add(conductor);
            }
        }
        var origen = new Ubicacion(-42.779125, -64.969858);
        var destino = new Ubicacion(-42.591067, -64.876119);
        var alternativas = gestorServicios.obtenerAlternativas(origen, destino,
                servicio.getTipoServicio(), servicio.getCategoriaVehiculo());
        var alternativa = alternativas.stream()
                .filter(a -> a.getServicio() == servicio)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay una alternativa compatible con el primer servicio."));

        ColaSolicitudes cola = new ColaSolicitudes(conductoresCompetidores.size());

        System.out.println("=== DEMOSTRACIÓN DE VIAJE CON PRODUCTOR/CONSUMIDOR ===");
        System.out.printf("Datos cargados: clientes simulados=%d, conductores competidores=%d%n",
                cantidadClientes, conductoresCompetidores.size());
        ExecutorService executor = Executors.newFixedThreadPool(
                conductoresCompetidores.size() + 1);
        executor.submit(new ProductorSolicitudes(
                cola, clientes, alternativa, cantidadClientes));
        for (Usuario conductor : conductoresCompetidores) {
            executor.submit(new ConsumidorSolicitudes(
                    cola, gestorViajes, despachador, conductor));
        }
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            executor.shutdownNow();
            throw new IllegalStateException("La demostración de concurrencia excedió el tiempo límite.");
        }
        System.out.println("=== DEMOSTRACIÓN FINALIZADA ===");
    }

    private static int leerCantidadClientes() {
        Scanner entrada = new Scanner(System.in);
        System.out.print("¿Cuántos clientes quieren viajar? ");
        int cantidad = entrada.nextInt();
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de clientes debe ser mayor que cero.");
        }
        return cantidad;
    }

    private static int cantidadCompatibles(List<Usuario> conductores,
                                           Despachador despachador,
                                           movilidaddigital.modelo.Servicio servicio) {
        int cantidad = 0;
        for (Usuario conductor : conductores) {
            if (despachador.esCompatible(conductor.getConductor(), servicio)) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
