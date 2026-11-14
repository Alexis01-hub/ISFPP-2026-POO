package movilidaddigital.datos;

import movilidaddigital.modelo.TipoVehiculo;
import movilidaddigital.modelo.Ubicacion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provee acceso tipado a la configuración de la aplicación.
 *
 * <p>Las propiedades se leen desde {@code config.properties}, incluyendo la
 * zona geográfica y el centro inicial del mapa.</p>
 */
public final class Configuracion {
    private Configuracion() {
    }

    public static boolean ubicacionDentroZona(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicacion no puede ser null");
        }
        Properties propiedades = cargarProperties("config.properties");
        double latitudMinima = Math.min(leerDouble(propiedades, "latitud1"),
                leerDouble(propiedades, "latitud2"));
        double latitudMaxima = Math.max(leerDouble(propiedades, "latitud1"),
                leerDouble(propiedades, "latitud2"));
        double longitudMinima = Math.min(leerDouble(propiedades, "longitud1"),
                leerDouble(propiedades, "longitud2"));
        double longitudMaxima = Math.max(leerDouble(propiedades, "longitud1"),
                leerDouble(propiedades, "longitud2"));
        validarLimites(latitudMinima, latitudMaxima, -90, 90, "latitud");
        validarLimites(longitudMinima, longitudMaxima, -180, 180, "longitud");
        return ubicacion.getLatitud() >= latitudMinima
                && ubicacion.getLatitud() <= latitudMaxima
                && ubicacion.getLongitud() >= longitudMinima
                && ubicacion.getLongitud() <= longitudMaxima;
    }

    public static double latitudMinima() {
        return Math.min(limite("latitud1"), limite("latitud2"));
    }

    public static double latitudMaxima() {
        return Math.max(limite("latitud1"), limite("latitud2"));
    }

    public static double longitudMinima() {
        return Math.min(limite("longitud1"), limite("longitud2"));
    }

    public static double longitudMaxima() {
        return Math.max(limite("longitud1"), limite("longitud2"));
    }

    private static double limite(String clave) {
        double valor = leerDouble(cargarProperties("config.properties"), clave);
        double minimo = clave.startsWith("latitud") ? -90 : -180;
        double maximo = clave.startsWith("latitud") ? 90 : 180;
        if (!Double.isFinite(valor) || valor < minimo || valor > maximo) {
            throw new IllegalStateException("La propiedad " + clave + " esta fuera de rango");
        }
        return valor;
    }

    private static void validarLimites(double minimo, double maximo,
                                       double limiteInferior, double limiteSuperior,
                                       String nombre) {
        if (!Double.isFinite(minimo) || !Double.isFinite(maximo)
                || minimo < limiteInferior || maximo > limiteSuperior) {
            throw new IllegalStateException("Limites de " + nombre + " fuera de rango");
        }
    }

    public static double latitudCentroMapa() {
        return leerDouble(cargarProperties("config.properties"), "latitudCentro");
    }

    public static double longitudCentroMapa() {
        return leerDouble(cargarProperties("config.properties"), "longitudCentro");
    }

    public static int zoomMapa() {
        double zoom = leerDouble(cargarProperties("config.properties"), "zoomMapa");
        if (zoom < 0 || zoom > 19 || zoom != Math.rint(zoom)) {
            throw new IllegalStateException("La propiedad zoomMapa debe ser un entero entre 0 y 19");
        }
        return (int) zoom;
    }

    public static String archivoUsuarios() {
        return leerTexto(cargarProperties("config.properties"), "usuario");
    }

    public static String archivoServicios() {
        return leerTexto(cargarProperties("config.properties"), "servicio");
    }

    public static String archivoVehiculos() {
        return leerTexto(cargarProperties("config.properties"), "vehiculo");
    }

    public static String origenDatos() {
        return cargarProperties("config.properties").getProperty("origen.datos", "TXT").trim();
    }

    public static String baseDatosUrl() {
        return propiedadBaseDatos("url");
    }

    public static String baseDatosUsuario() {
        return propiedadBaseDatos("usr");
    }

    public static String baseDatosPassword() {
        return propiedadBaseDatos("pwd");
    }

    public static String baseDatosSchema() {
        return propiedadBaseDatos("schema");
    }

    private static String propiedadBaseDatos(String clave) {
        return leerTexto(cargarProperties("config.properties"), clave);
    }

    /**
     * Velocidad promedio (km/h) usada para estimar el tiempo de un viaje.
     * Se lee de velocidadAuto / velocidadMoto; si no estan en el archivo, usa 30 y 35.
     */
    public static double velocidadPromedioKmH(TipoVehiculo tipo) {
        Properties propiedades = cargarProperties("config.properties");
        String clave = tipo == TipoVehiculo.MOTO ? "velocidadMoto" : "velocidadAuto";
        double porDefecto = tipo == TipoVehiculo.MOTO ? 35 : 30;
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        try {
            double v = Double.parseDouble(valor.trim());
            if (v <= 0) {
                throw new IllegalStateException("La propiedad " + clave + " debe ser mayor que 0");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("La propiedad " + clave + " no es numerica: " + valor, e);
        }
    }

    private static Properties cargarProperties(String nombreRecurso) {
        Properties propiedades = new Properties();

        try (InputStream entrada = Configuracion.class
                .getClassLoader()
                .getResourceAsStream(nombreRecurso)) {
            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontro el recurso: " + nombreRecurso);
            }
            propiedades.load(entrada);
            return propiedades;
        } catch (IOException excepcion) {
            throw new IllegalStateException(
                    "No se pudo leer el recurso: " + nombreRecurso, excepcion);
        }
    }

    private static String leerTexto(Properties propiedades, String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta la propiedad obligatoria: " + clave);
        }
        return valor.trim();
    }

    private static double leerDouble(Properties propiedades, String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta la propiedad obligatoria: " + clave);
        }

        try {
            return Double.parseDouble(valor.trim());
        } catch (NumberFormatException excepcion) {
            throw new IllegalStateException(
                    "La propiedad " + clave + " no es numerica: " + valor,
                    excepcion);
        }
    }
}
