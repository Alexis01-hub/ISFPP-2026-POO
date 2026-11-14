package movilidaddigital.interfaz.mapa;

import movilidaddigital.modelo.Ubicacion;

import java.util.function.BiConsumer;

public final class PuenteSeleccionMapa {
    private final BiConsumer<String, Ubicacion> receptor;

    public PuenteSeleccionMapa(BiConsumer<String, Ubicacion> receptor) {
        this.receptor = receptor;
    }

    public void seleccionarPunto(String tipo, double latitud, double longitud) {
        if (!"origen".equals(tipo) && !"destino".equals(tipo)) {
            return;
        }
        if (Double.isFinite(latitud) && Double.isFinite(longitud)) {
            receptor.accept(tipo, new Ubicacion(latitud, longitud));
        }
    }
}
