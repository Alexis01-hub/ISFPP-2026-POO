package movilidaddigital.aplicacion;

import movilidaddigital.datos.CargadorDatos;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.enums.CalificacionViaje;
import movilidaddigital.modelo.enums.CategoriaVehiculo;
import movilidaddigital.modelo.enums.TipoServicio;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.util.List;
import net.datastructures.Map;

import java.time.LocalDateTime;

/** Prueba rapida de la capa de negocio con los datos de los archivos. */
public class Main {
    public static void main(String[] args) {
        // 1. Cargar datos
        CargadorDatos cargador = new CargadorDatos();
        GestorServicios gestorServicios = new GestorServicios(cargador.cargarServicios());
        Map<String, Vehiculo> vehiculos = cargador.cargarVehiculos();
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cargador.cargarUsuarios(vehiculos));
        GestorViajes gestorViajes = new GestorViajes(gestorServicios, new Despachador(gestorUsuarios));
        for (String aviso : cargador.getAdvertencias()) {
            System.out.println("AVISO: " + aviso);
        }
        System.out.println("Usuarios cargados: " + gestorUsuarios.getUsuarios().size());

        Usuario paula = gestorUsuarios.buscarPorEmail("paula.vazquez@email.com"); // cliente
        Usuario juan = gestorUsuarios.buscarPorEmail("juan.perez@email.com"); // conductor
        LocalDateTime ahora = LocalDateTime.of(2026, 10, 7, 10, 0);

        // 2. Paula pide un viaje: ve las alternativas
        Ubicacion origen = new Ubicacion(-42.65, -64.89);
        Ubicacion destino = new Ubicacion(-42.86, -64.88);
        List<Alternativa> alternativas = gestorServicios.obtenerAlternativas(
                origen, destino, TipoServicio.PASAJEROS, CategoriaVehiculo.PREMIUM);
        for (Alternativa a : alternativas) {
            System.out.println("Alternativa -> " + a);
        }

        // 3. Elige la primera y la solicita
        Viaje viaje = gestorViajes.solicitar(paula, alternativas.get(0), ahora);
        System.out.println("Estado: " + viaje.estadoActual());

        // 4. Juan se pone disponible y acepta
        gestorUsuarios.ponerDisponible(juan);
        gestorViajes.aceptar(viaje, juan, ahora.plusMinutes(1));
        System.out.println("Estado: " + viaje.estadoActual() + " | Juan: " + juan.getConductor().getEstadoConductor());

        // 5. Inicia y finaliza
        gestorViajes.iniciar(viaje, ahora.plusMinutes(10));
        double costo = gestorViajes.finalizar(viaje, ahora.plusMinutes(40),
                CalificacionViaje.EXCELENTE, CalificacionViaje.BUENO);
        System.out.println("Estado: " + viaje.estadoActual() + " | Costo final: $" + String.format("%.2f", costo));
        System.out.println("Juan: " + juan.getConductor().getEstadoConductor());
    }
}