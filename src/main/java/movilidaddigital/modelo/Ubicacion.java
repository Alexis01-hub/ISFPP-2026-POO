package movilidaddigital.modelo;

public class Ubicacion {
    private final double latitud;
    private final double longitud;

    public Ubicacion(double latitud, double longitud) {
        validarCoordenadas(latitud, longitud);
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    /**
     * Calcula la distancia sobre la superficie terrestre usando Haversine.
     *
     * @return distancia en kilometros
     */
    public double calcularDistancia(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicacion no puede ser null");
        }

        final double radioTierraKm = 6371.0;
        double diferenciaLatitud = Math.toRadians(ubicacion.latitud - latitud);
        double diferenciaLongitud = Math.toRadians(ubicacion.longitud - longitud);
        double latitudInicial = Math.toRadians(latitud);
        double latitudFinal = Math.toRadians(ubicacion.latitud);

        double haversine = Math.pow(Math.sin(diferenciaLatitud / 2), 2)
                + Math.cos(latitudInicial) * Math.cos(latitudFinal)
                * Math.pow(Math.sin(diferenciaLongitud / 2), 2);

        return 2 * radioTierraKm * Math.asin(Math.sqrt(haversine));
    }

    public boolean estaDentro(RectanguloGeografico rectangulo) {
        if (rectangulo == null) {
            throw new IllegalArgumentException("El rectangulo no puede ser null");
        }
        return rectangulo.contiene(this);
    }

    private static void validarCoordenadas(double latitud, double longitud) {
        if (!Double.isFinite(latitud) || latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90");
        }
        if (!Double.isFinite(longitud) || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180");
        }
    }

    @Override
    public String toString() {
        return "Ubicacion [latitud=" + latitud + ", longitud=" + longitud + "]";
    }
}
