package movilidaddigital.negocio;

import movilidaddigital.modelo.Conductor;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.enums.EstadoConductor;

import java.util.ArrayList;
import java.util.List;

/** Decide que conductores pueden tomar un viaje. */
public class Despachador {
    private final GestorUsuarios gestorUsuarios;

    public Despachador(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
    }

    /**
     * Un conductor es compatible con un servicio si su vehiculo activo es del mismo tipo (auto/moto),
     * presta ese tipo de servicio y la categoria del servicio esta entre la categoria
     * minima que eligio el conductor y la categoria de su vehiculo.
     * @param conductor El conductor a evaluar
     * @param servicio El servicio a evaluar
     * @return true si el conductor es compatible con el servicio, false en caso contrario
     */
    public boolean esCompatible(Conductor conductor, Servicio servicio) {
        Vehiculo v = conductor.getVehiculoActivo();
        if (v.getTipoVehiculo() != servicio.getTipoVehiculo()) {
            return false;
        }
        boolean prestaServicio = false;
        for (var t : v.getTipoServicios()) {
            if (t == servicio.getTipoServicio()) prestaServicio = true;
        }
        int cat = servicio.getCategoriaVehiculo().getValor();
        return prestaServicio
                && cat >= conductor.getCategoriaVehiculoActivo().getValor()
                && cat <= v.getCategoriaVehiculo().getValor();
    }

    /**
     * Devuelve la lista de conductores DISPONIBLES y compatibles con el viaje (nunca el propio cliente).
     * @param viaje El viaje a evaluar
     * @return Lista de conductores elegibles para el viaje
     */
    public List<Usuario> conductoresElegibles(Viaje viaje) {
        List<Usuario> elegibles = new ArrayList<>();
        for (Usuario u : gestorUsuarios.getUsuarios()) {
            Conductor c = u.getConductor();
            if (c != null && u != viaje.getCliente()
                    && c.getEstadoConductor() == EstadoConductor.DISPONIBLE
                    && esCompatible(c, viaje.getServicio())) {
                elegibles.add(elegibles.size(), u);
            }
        }
        return elegibles;
    }
}