import movilidaddigital.excepciones.EstadoConductorInvalidoException;
import movilidaddigital.modelo.CategoriaVehiculo;
import movilidaddigital.modelo.EstadoConductor;
import movilidaddigital.modelo.RolUsuario;
import movilidaddigital.modelo.TipoServicio;
import movilidaddigital.modelo.TipoVehiculo;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.negocio.GestorUsuarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GestorUsuariosTest {

    @Test
    @DisplayName("Un conductor puede pasar a disponible y volver fuera de servicio")
    void cambiaDisponibilidadDelConductor() {
        Usuario usuario = conductor();
        GestorUsuarios gestor = new GestorUsuarios();

        gestor.ponerDisponible(usuario);
        assertEquals(EstadoConductor.DISPONIBLE,
                usuario.getConductor().getEstadoConductor());

        gestor.ponerFueraDeServicio(usuario);
        assertEquals(EstadoConductor.FUERA_DE_SERVICIO,
                usuario.getConductor().getEstadoConductor());
    }

    @Test
    @DisplayName("No permite cambiar la disponibilidad durante un viaje")
    void rechazaCambioDuranteViaje() {
        Usuario usuario = conductor();
        usuario.cambiarRolActivo(RolUsuario.CONDUCTOR);
        usuario.getConductor().setEstado(EstadoConductor.VIAJE_A_ORIGEN);
        GestorUsuarios gestor = new GestorUsuarios();

        assertThrows(EstadoConductorInvalidoException.class,
                () -> gestor.ponerFueraDeServicio(usuario));
    }

    private Usuario conductor() {
        Usuario usuario = new Usuario("Ana", "123", "ana@test.com");
        Vehiculo vehiculo = new Vehiculo(
                "ABC123", "Auto", 4, TipoVehiculo.AUTO,
                CategoriaVehiculo.ESTANDAR, TipoServicio.PASAJEROS);
        usuario.altaConductor("LIC-123", vehiculo);
        return usuario;
    }
}
