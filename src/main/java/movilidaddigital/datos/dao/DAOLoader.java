package movilidaddigital.datos.dao;

import java.util.Locale;
import movilidaddigital.datos.Configuracion;
import movilidaddigital.datos.dao.txt.TxtDAOFactory;
import movilidaddigital.datos.dao.bd.BdDAOFactory;

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
        return getFactory(Configuracion.origenDatos());
    }

    public static DAOFactory getFactory(String origen) {
        return crearFactory(origen);
    }

    private static DAOFactory crearFactory() {
        return crearFactory(Configuracion.origenDatos());
    }

    private static DAOFactory crearFactory(String origenTexto) {
        String origen = origenTexto.trim().toUpperCase(Locale.ROOT);
        if ("BD".equals(origen) || "SQL".equals(origen)) {
            return new BdDAOFactory(Configuracion.baseDatosUrl(),
                    Configuracion.baseDatosUsuario(), Configuracion.baseDatosPassword(),
                    Configuracion.baseDatosSchema());
        }
        if (!"TXT".equals(origen)) {
            throw new IllegalStateException("Origen de datos no soportado: " + origen);
        }
        return new TxtDAOFactory(Configuracion.archivoServicios(),
                Configuracion.archivoVehiculos(), Configuracion.archivoUsuarios());
    }
}
