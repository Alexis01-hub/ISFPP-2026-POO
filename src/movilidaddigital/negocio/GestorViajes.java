package movilidaddigital.negocio;
import movilidaddigital.excepciones.ClienteEnViajeException;
import movilidaddigital.excepciones.ConductorNoDisponibleException;
import movilidaddigital.excepciones.TransicionViajeInvalidaException;
import movilidaddigital.excepciones.VehiculoNoCompatibleException;
import movilidaddigital.modelo.RegistroViaje;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.enums.CalificacionViaje;
import movilidaddigital.modelo.enums.EstadoConductor;
import movilidaddigital.modelo.enums.EstadoViaje;
import movilidaddigital.modelo.enums.RolUsuario;

import net.datastructures.ArrayList;
import net.datastructures.List;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Maneja el ciclo de vida de los viajes y el estado de los conductores que participan.
 * Los metodos son synchronized: cuando haya hilos (varios conductores aceptando a la vez),
 * solo uno puede ganar el viaje.
 */
public class GestorViajes {
    private final GestorServicios gestorServicios;
    private final Despachador despachador;
    private final List<Viaje> viajes = new ArrayList<>();

    public GestorViajes(GestorServicios gestorServicios, Despachador despachador) {
        this.gestorServicios = gestorServicios;
        this.despachador = despachador;
    }

    public List<Viaje> getViajes() {
        return viajes;
    }

    /** El cliente pide el viaje con la alternativa elegida. Un cliente no puede tener dos viajes en curso. */
    public synchronized Viaje solicitar(Usuario cliente, Alternativa alternativa, LocalDateTime fecha) {
        if (cliente.getCliente().enViaje()) {
            throw new ClienteEnViajeException("El cliente " + cliente.getNombre() + " ya tiene un viaje en curso");
        }
        Viaje viaje = new Viaje(cliente, alternativa.getOrigen(), alternativa.getDestino(), alternativa.getServicio());
        viaje.solicitar(fecha);
        viajes.add(viajes.size(), viaje);
        return viaje;
    }

    /** Un conductor disponible y compatible acepta el viaje; pasa a VIAJE_A_ORIGEN. */
    public synchronized void aceptar(Viaje viaje, Usuario conductor, LocalDateTime fecha) {
        if (conductor.getConductor() == null) {
            throw new TransicionViajeInvalidaException("El usuario no es conductor");
        }
        if (conductor == viaje.getCliente()) {
            throw new ConductorNoDisponibleException("Un conductor no puede aceptar su propio viaje");
        }
        if (conductor.getConductor().getEstadoConductor() != EstadoConductor.DISPONIBLE) {
            throw new ConductorNoDisponibleException("El conductor " + conductor.getNombre() + " no esta disponible");
        }
        if (!despachador.esCompatible(conductor.getConductor(), viaje.getServicio())) {
            throw new VehiculoNoCompatibleException("El vehiculo activo no es compatible con el servicio " + viaje.getServicio().getNombre());
        }
        viaje.aceptar(fecha, conductor); // si otro ya lo acepto, falla aca y el conductor no cambia de estado
        conductor.getConductor().setEstadoConductor(EstadoConductor.VIAJE_A_ORIGEN);
    }

    /** El cliente sube: el conductor pasa a VIAJE_A_DESTINO. */
    public synchronized void iniciar(Viaje viaje, LocalDateTime fecha) {
        viaje.iniciar(fecha);
        viaje.getConductor().getConductor().setEstadoConductor(EstadoConductor.VIAJE_A_DESTINO);
    }

    /** Termina el viaje con las calificaciones mutuas; el conductor queda DISPONIBLE. */
    public synchronized double finalizar(Viaje viaje, LocalDateTime fecha,
                                         CalificacionViaje calificacionConductor, CalificacionViaje calificacionCliente) {
        viaje.finalizar(fecha, calificacionConductor, calificacionCliente);
        viaje.getConductor().getConductor().setEstadoConductor(EstadoConductor.DISPONIBLE);
        return costoFinal(viaje);
    }

    /**
     * Cancela el viaje y devuelve lo que se cobra por la cancelacion.
     * Si cancela el conductor, queda FUERA_DE_SERVICIO; si cancela el cliente, el conductor queda DISPONIBLE.
     */
    public synchronized double cancelar(Viaje viaje, Usuario quien, String motivo, LocalDateTime fecha) {
        viaje.cancelar(fecha, quien, motivo);
        double costo = calcularCostoCancelacion(viaje);
        Usuario conductor = viaje.getConductor();
        if (conductor != null) {
            conductor.getConductor().setEstadoConductor(
                    viaje.getRolCancela() == RolUsuario.CONDUCTOR ? EstadoConductor.FUERA_DE_SERVICIO : EstadoConductor.DISPONIBLE);
        }
        return costo;
    }

    /** Nadie acepto a tiempo: el viaje pasa a RECHAZADO. */
    public synchronized void rechazarPorTimeout(Viaje viaje, LocalDateTime fecha) {
        viaje.rechazar(fecha);
    }

    /**
     * Costo de cancelar: 0 si cancela el conductor o si nadie habia aceptado todavia.
     * Si cancela el cliente con el conductor ya en camino, se cobra el tramo que el conductor
     * alcanzo a recorrer (tiempo entre ACEPTADO y CANCELADO a la velocidad promedio),
     * sin tarifa base: precioKm * km + precioMinuto * minutos.
     */
    public double calcularCostoCancelacion(Viaje viaje) {
        if (viaje.estadoActual() != EstadoViaje.CANCELADO || viaje.getRolCancela() != RolUsuario.CLIENTE) {
            return 0;
        }
        LocalDateTime aceptado = fechaDe(viaje, EstadoViaje.ACEPTADO);
        if (aceptado == null) {
            return 0;
        }
        double minutos = minutosEntre(aceptado, fechaDe(viaje, EstadoViaje.CANCELADO));
        Servicio s = viaje.getServicio();
        double km = gestorServicios.velocidadPromedio(s.getTipoVehiculo()) * minutos / 60;
        return s.getPrecioKm() * km + s.getPrecioMinuto() * minutos;
    }

    /** Costo de un viaje FINALIZADO: distancia origen-destino y minutos entre INICIADO y FINALIZADO. */
    public double costoFinal(Viaje viaje) {
        if (viaje.estadoActual() != EstadoViaje.FINALIZADO) {
            throw new TransicionViajeInvalidaException("Solo se calcula el costo final de un viaje FINALIZADO");
        }
        double km = viaje.getOrigen().calcularDistancia(viaje.getDestino());
        double minutos = minutosEntre(fechaDe(viaje, EstadoViaje.INICIADO), fechaDe(viaje, EstadoViaje.FINALIZADO));
        return viaje.getServicio().calcularCosto(km, minutos);
    }

    private LocalDateTime fechaDe(Viaje viaje, EstadoViaje estado) {
        for (RegistroViaje r : viaje.getRegistroViaje()) {
            if (r.getEstadoViaje() == estado) {
                return r.getFechaHora();
            }
        }
        return null;
    }

    private double minutosEntre(LocalDateTime desde, LocalDateTime hasta) {
        return Duration.between(desde, hasta).toSeconds() / 60.0;
    }
}
