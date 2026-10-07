package movilidaddigital.datos;

import movilidaddigital.modelo.RectanguloGeografico;
import movilidaddigital.modelo.enums.TipoVehiculo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuracion {
    private Configuracion() {
    }

    public static RectanguloGeografico cargarZonaPermitida() {
        Properties propiedades = cargarProperties("config.properties");

        return new RectanguloGeografico(
                leerDouble(propiedades, "latitud1"),
                leerDouble(propiedades, "longitud1"),
                leerDouble(propiedades, "latitud2"),
                leerDouble(propiedades, "longitud2"));
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
