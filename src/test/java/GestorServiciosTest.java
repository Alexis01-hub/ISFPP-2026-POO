import movilidaddigital.excepciones.UbicacionFueraDelElejidoException;
import movilidaddigital.modelo.CategoriaVehiculo;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.TipoServicio;
import movilidaddigital.modelo.TipoVehiculo;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.GestorServicios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestorServiciosTest {

    @Test
    @DisplayName("Obtiene alternativas compatibles dentro del ejido")
    void obtieneAlternativasDentroDelEjido() {
        Servicio servicio = new Servicio(
                "Estandar", 2000, 200, 100,
                TipoVehiculo.AUTO, CategoriaVehiculo.ESTANDAR,
                TipoServicio.PASAJEROS);
        GestorServicios gestor = new GestorServicios(List.of(servicio));

        List<Alternativa> alternativas = gestor.obtenerAlternativas(
                new Ubicacion(-42.76, -65.03),
                new Ubicacion(-42.78, -65.05),
                TipoServicio.PASAJEROS,
                CategoriaVehiculo.ESTANDAR);

        assertEquals(1, alternativas.size());
        assertEquals(servicio, alternativas.get(0).getServicio());
        assertTrue(alternativas.get(0).getDistanciaKm() > 0);
        assertTrue(alternativas.get(0).getCosto() > servicio.getTarifaBase());
    }

    @Test
    @DisplayName("Rechaza ubicaciones fuera del ejido configurado")
    void rechazaUbicacionFueraDelEjido() {
        Servicio servicio = new Servicio(
                "Estandar", 2000, 200, 100,
                TipoVehiculo.AUTO, CategoriaVehiculo.ESTANDAR,
                TipoServicio.PASAJEROS);
        GestorServicios gestor = new GestorServicios(List.of(servicio));

        assertThrows(UbicacionFueraDelElejidoException.class, () ->
                gestor.obtenerAlternativas(
                        new Ubicacion(-43.30, -65.03),
                        new Ubicacion(-42.78, -65.05),
                        TipoServicio.PASAJEROS,
                        CategoriaVehiculo.ESTANDAR));
    }
}
