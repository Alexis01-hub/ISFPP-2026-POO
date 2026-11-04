package movilidaddigital.interfaz;

import movilidaddigital.datos.Configuracion;
import movilidaddigital.modelo.RectanguloGeografico;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.enums.CalificacionViaje;
import movilidaddigital.modelo.enums.EstadoConductor;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConsolaUI implements InterfazUsuario {
    private final GestorServicios gestorServicios;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;

    public ConsolaUI(GestorServicios gestorServicios, GestorUsuarios gestorUsuarios,
                     GestorViajes gestorViajes) {
        this.gestorServicios = gestorServicios;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @Override
    public void iniciar() {
        System.out.println("\n=== MOVILIDAD DIGITAL - CONSOLA ===");
        System.out.println("Los datos fueron cargados correctamente.");
        simularViajeAleatorio();
    }

    private void simularViajeAleatorio() {
        RectanguloGeografico zona = Configuracion.cargarZonaPermitida();
        Ubicacion origen = ubicacionAleatoria(zona);
        Ubicacion destino = ubicacionAleatoria(zona);

        List<Usuario> conductores = new ArrayList<>();
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario.getConductor() != null) {
                conductores.add(usuario);
            }
        }
        if (conductores.isEmpty() || gestorServicios.getServicios().isEmpty()) {
            throw new IllegalStateException("No hay conductores o servicios cargados.");
        }

        List<Usuario> clientes = new ArrayList<>(gestorUsuarios.getUsuarios());
        Collections.shuffle(clientes);
        Collections.shuffle(conductores);

        for (Usuario conductor : conductores) {
            conductor.getConductor().setEstadoConductor(EstadoConductor.DISPONIBLE);
        }

        LocalDateTime inicio = LocalDateTime.now();
        for (Usuario cliente : clientes) {
            for (Servicio servicio : gestorServicios.getServicios()) {
                List<Alternativa> alternativas = gestorServicios.obtenerAlternativas(
                        origen, destino, servicio.getTipoServicio(),
                        servicio.getCategoriaVehiculo());
                if (alternativas.isEmpty()) {
                    continue;
                }
                for (Usuario conductor : conductores) {
                    if (conductor == cliente || !compatible(conductor, servicio)) {
                        continue;
                    }
                    ejecutarViaje(cliente, conductor, alternativas.get(0), inicio);
                    return;
                }
            }
        }
        throw new IllegalStateException("No se encontró una combinación compatible de cliente y conductor.");
    }

    private void ejecutarViaje(Usuario cliente, Usuario conductor,
                               Alternativa alternativa, LocalDateTime inicio) {
        Viaje viaje = gestorViajes.solicitar(cliente, alternativa, inicio);
        gestorViajes.aceptar(viaje, conductor, inicio.plusMinutes(1));
        gestorViajes.iniciar(viaje, inicio.plusMinutes(2));
        double costo = gestorViajes.finalizar(viaje, inicio.plusMinutes(20),
                CalificacionViaje.EXCELENTE, CalificacionViaje.BUENO);

        System.out.println("\n--- Viaje simulado ---");
        System.out.println("Cliente: " + cliente.getNombre() + " (" + cliente.getEmail() + ")");
        System.out.println("Conductor: " + conductor.getNombre() + " (" + conductor.getEmail() + ")");
        System.out.println("Servicio: " + alternativa.getServicio().getNombre());
        System.out.printf("Recorrido: %.2f km | Costo: $%.2f%n",
                alternativa.getDistanciaKm(), costo);
        System.out.println("Estado final: " + viaje.estadoActual());
    }

    private boolean compatible(Usuario usuario, Servicio servicio) {
        var vehiculo = usuario.getConductor().getVehiculoActivo();
        return vehiculo.getTipoVehiculo() == servicio.getTipoVehiculo()
                && vehiculo.getCategoriaVehiculo().getValor()
                >= servicio.getCategoriaVehiculo().getValor()
                && vehiculo.getTipoServicios().contains(servicio.getTipoServicio());
    }

    private Ubicacion ubicacionAleatoria(RectanguloGeografico zona) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Ubicacion(
                random.nextDouble(zona.getLatitudMinima(), zona.getLatitudMaxima()),
                random.nextDouble(zona.getLongitudMinima(), zona.getLongitudMaxima()));
    }
}
