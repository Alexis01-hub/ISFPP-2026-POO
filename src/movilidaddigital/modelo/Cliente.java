package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.EstadoViaje;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private List<Viaje> viajes;

    public Cliente() {
        this.viajes = new ArrayList<>();
    }

    private boolean enViaje(){
        for( Viaje viaje : viajes){
            EstadoViaje estado = viaje.estadoActual();
            if (estado == EstadoViaje.SOLICITADO ||
            estado == EstadoViaje.ACEPTADO ||
            estado == EstadoViaje.INICIADO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Agrega un viaje a la lista de viajes del cliente
     * @param viaje el viaje a agregar
     */
    public void agregarViaje(Viaje viaje){
        viajes.add(viaje);
    }

    public List<Viaje> getViajes() {
        return viajes;
    }
}
