package movilidaddigital.negocio;

import java.time.LocalDateTime;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Viaje;

/**
 * Factory Method: las subclases deciden qué tipo concreto de viaje crear.
 * Hoy existe el viaje estándar; mañana permite agregar modalidades sin cambiar
 * GestorViajes.
 */
public abstract class ViajeFactory {
    protected abstract Viaje crearViaje(Usuario cliente, Ubicacion origen,
                                        Ubicacion destino, Servicio servicio);

    public final Viaje solicitar(Usuario cliente, Ubicacion origen, Ubicacion destino,
                                 Servicio servicio, LocalDateTime fecha) {
        Viaje viaje = crearViaje(cliente, origen, destino, servicio);
        viaje.solicitar(fecha);
        return viaje;
    }
}
