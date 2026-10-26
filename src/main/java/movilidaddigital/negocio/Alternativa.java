package movilidaddigital.negocio;

import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Ubicacion;

/**
 * Una opcion que se le muestra al cliente antes de pedir el viaje:
 * un servicio con la distancia, el tiempo estimado y el costo para ese recorrido.
 */
public class Alternativa {
    private final Servicio servicio;
    private final Ubicacion origen;
    private final Ubicacion destino;
    private final double distanciaKm;
    private final double tiempoMinutos;
    private final double costo;

    public Alternativa(Servicio servicio, Ubicacion origen, Ubicacion destino,
                       double distanciaKm, double tiempoMinutos, double costo) {
        this.servicio = servicio;
        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
        this.tiempoMinutos = tiempoMinutos;
        this.costo = costo;
    }

    public Servicio getServicio() { return servicio; }
    public Ubicacion getOrigen() { return origen; }
    public Ubicacion getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
    public double getTiempoMinutos() { return tiempoMinutos; }
    public double getCosto() { return costo; }

    @Override
    public String toString() {
        return String.format("%s: %.1f km, %.0f min, $%.2f",
                servicio.getNombre(), distanciaKm, tiempoMinutos, costo);
    }
}