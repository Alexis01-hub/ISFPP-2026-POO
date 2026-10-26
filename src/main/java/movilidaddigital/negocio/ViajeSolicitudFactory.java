package movilidaddigital.negocio;

import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Viaje;

public class ViajeSolicitudFactory extends ViajeFactory {
    @Override
    protected Viaje crearViaje(Usuario cliente, Ubicacion origen,
                               Ubicacion destino, Servicio servicio) {
        return new Viaje(cliente, origen, destino, servicio);
    }
}
