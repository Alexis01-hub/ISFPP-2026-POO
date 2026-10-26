package aplicacion;

import datos.CargadorDatos;
import datos.RepositorioDatos;
import modelo.Ubicacion;
import modelo.Usuario;
import modelo.Vehiculo;
import modelo.Viaje;
import modelo.enums.CalificacionViaje;
import modelo.enums.CategoriaVehiculo;
import modelo.enums.TipoServicio;
import negocio.Alternativa;
import negocio.Despachador;
import negocio.GestorServicios;
import negocio.GestorUsuarios;
import negocio.GestorViajes;

import java.util.List;
import movilidaddigital.datastructures.Map;

import java.time.LocalDateTime;

/** Prueba rapida de la capa de negocio con los datos de los archivos. */
public class Main {
    public static void main(String[] args) {
        // La aplicacion ya queda desacoplada del origen: TXT puede reemplazarse por BD
        // mediante DAOLoader sin cambiar la logica de negocio.
        try {
            RepositorioDatos repositorio = new RepositorioDatos();
            System.out.println("DTO disponibles: servicios=" + repositorio.obtenerServicios().size()
                    + ", vehiculos=" + repositorio.obtenerVehiculos().size()
                    + ", usuarios=" + repositorio. obtenerUsuarios().size());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron leer los datos mediante DAO", e);
        }
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