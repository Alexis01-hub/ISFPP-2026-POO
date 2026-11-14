package movilidaddigital.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfil de cliente y registro de los viajes solicitados por el usuario.
 */
public class Cliente {
    private List<Viaje> viajes;

    /**
     * Crea un cliente sin viajes registrados.
     */
    public Cliente() {
        this.viajes = new ArrayList<>();
    }

    /**
     * Indica si el cliente tiene un viaje todavía activo.
     *
     * @return {@code true} si el último estado de algún viaje es solicitado,
     * aceptado o iniciado
     */
    public boolean enViaje(){
        if (viajes.isEmpty())
            return false;

        Viaje ultimoViaje = viajes.get(viajes.size() - 1);
        EstadoViaje estado = ultimoViaje.estadoActual();

        return estado == EstadoViaje.SOLICITADO || estado == EstadoViaje.ACEPTADO || estado == EstadoViaje.INICIADO;
    }

    /**
     * Agrega un viaje a la lista de viajes del cliente
     * @param viaje el viaje a agregar
     */
    /**
     * Agrega un viaje al historial del cliente.
     *
     * @param viaje viaje que se incorpora al historial
     */
    public void agregarViaje(Viaje viaje){
        viajes.add(viaje);
    }

    /**
     * Devuelve los viajes asociados al cliente.
     *
     * @return lista mutable del historial de viajes
     */
    public List<Viaje> getViajes() {
        return viajes;
    }
}
