package movilidaddigital.negocio;
import movilidaddigital.datos.Configuracion;
import movilidaddigital.excepciones.UbicacionFueraDelElejidoException;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.CategoriaVehiculo;
import movilidaddigital.modelo.TipoServicio;
import movilidaddigital.modelo.TipoVehiculo;

import java.util.ArrayList;
import java.util.List;

/** Conoce los servicios y arma las alternativas (distancia, tiempo y costo) para un recorrido. */
public class GestorServicios {
    private final List<Servicio> servicios;
    public GestorServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    /** Velocidad promedio (km/h) del tipo de vehiculo, tomada de la configuracion. */
    public double velocidadPromedio(TipoVehiculo tipo) {
        return Configuracion.velocidadPromedioKmH(tipo);
    }

    /**
     * Servicios que coinciden con lo pedido, cada uno con su costo para el recorrido.
     * @throws UbicacionFueraDelElejidoException si origen o destino estan fuera de la zona
     */
    public List<Alternativa> obtenerAlternativas(Ubicacion origen, Ubicacion destino,
                                                 TipoServicio tipoServicio, CategoriaVehiculo categoria) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Origen y destino son obligatorios");
        }
        if (!Configuracion.ubicacionDentroZona(origen)
                || !Configuracion.ubicacionDentroZona(destino)) {
            throw new UbicacionFueraDelElejidoException("El origen y el destino deben estar dentro del ejido");
        }
        double km = origen.calcularDistancia(destino);
        List<Alternativa> alternativas = new ArrayList<>();
        for (Servicio s : servicios) {
            if (s.getTipoServicio() == tipoServicio && s.getCategoriaVehiculo() == categoria) {
                double minutos = km / velocidadPromedio(s.getTipoVehiculo()) * 60;
                alternativas.add(alternativas.size(),
                        new Alternativa(s, origen, destino, km, minutos, s.calcularCosto(km, minutos)));
            }
        }
        return alternativas;
    }
}
