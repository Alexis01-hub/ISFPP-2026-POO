package movilidaddigital.modelo;

public class RectanguloGeografico {
    private final double latitudMinima;
    private final double latitudMaxima;
    private final double longitudMinima;
    private final double longitudMaxima;

    public RectanguloGeografico(double latitud1, double longitud1,
                                double latitud2, double longitud2) {
        // min/max permite que el archivo no dependa del orden de sus esquinas.
        this.latitudMinima = Math.min(latitud1, latitud2);
        this.latitudMaxima = Math.max(latitud1, latitud2);
        this.longitudMinima = Math.min(longitud1, longitud2);
        this.longitudMaxima = Math.max(longitud1, longitud2);

        validarLimites();
    }

    public boolean contiene(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicacion no puede ser null");
        }
        return ubicacion.getLatitud() >= latitudMinima
                && ubicacion.getLatitud() <= latitudMaxima
                && ubicacion.getLongitud() >= longitudMinima
                && ubicacion.getLongitud() <= longitudMaxima;
    }

    public double getLatitudMinima() {
        return latitudMinima;
    }

    public double getLatitudMaxima() {
        return latitudMaxima;
    }

    public double getLongitudMinima() {
        return longitudMinima;
    }

    public double getLongitudMaxima() {
        return longitudMaxima;
    }

    private void validarLimites() {
        validarCoordenada(latitudMinima, latitudMaxima, -90, 90, "latitud");
        validarCoordenada(longitudMinima, longitudMaxima, -180, 180, "longitud");
    }

    private static void validarCoordenada(double minima, double maxima,
                                          double limiteInferior,
                                          double limiteSuperior,
                                          String nombre) {
        if (!Double.isFinite(minima) || !Double.isFinite(maxima)
                || minima < limiteInferior || maxima > limiteSuperior) {
            throw new IllegalArgumentException(
                    "Limites de " + nombre + " fuera de rango");
        }
    }
}
