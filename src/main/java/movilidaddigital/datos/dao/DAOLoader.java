package movilidaddigital.datos.dao;

import java.util.Locale;
import movilidaddigital.datos.Configuracion;
import movilidaddigital.datos.dao.txt.TxtDAOFactory;

/**
 * Punto unico de acceso a la fabrica de persistencia.
 * La implementacion concreta se cambia por configuracion, no en la capa de negocio.
 */
public final class DAOLoader {
    private DAOLoader() { }

    private static class Holder {
        private static final DAOFactory INSTANCE = crearFactory();
    }

    public static DAOFactory getFactory() {
        return Holder.INSTANCE;
    }

    private static DAOFactory crearFactory() {
        String origen = Configuracion.origenDatos().toUpperCase(Locale.ROOT);
        if (!"TXT".equals(origen)) {
            throw new IllegalStateException(
                    "El origen '" + origen + "' aun no tiene implementacion. "
                            + "Cuando se definan las tablas se debe agregar la BdDAOFactory.");
        }
        return new TxtDAOFactory(Configuracion.archivoServicios(),
                Configuracion.archivoVehiculos(), Configuracion.archivoUsuarios());
    }
}
