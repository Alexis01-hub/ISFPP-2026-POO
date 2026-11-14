package movilidaddigital.interfaz.mapa;

import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import movilidaddigital.datos.Configuracion;
import movilidaddigital.modelo.Ubicacion;

import java.util.function.BiConsumer;

/**
 * Adaptador JavaFX que comunica la aplicación con el mapa Leaflet embebido.
 */
public final class VisualizadorRutasLeaflet {
    private final WebView webView;
    private final PuenteSeleccionMapa puente;
    private boolean paginaLista;
    private String dibujoPendiente;
    private String seleccionPendiente;
    private BiConsumer<String, Ubicacion> receptor;

    public VisualizadorRutasLeaflet(WebView webView) {
        this.webView = webView;
        this.puente = new PuenteSeleccionMapa(this::recibirPunto);
        var recurso = getClass().getResource("/ui/map/mapa.html");
        if (recurso == null) {
            throw new IllegalStateException("No se encontro /ui/map/mapa.html");
        }
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, anterior, estado) -> {
            if (estado == Worker.State.SUCCEEDED) {
                paginaLista = true;
                JSObject ventana = (JSObject) webView.getEngine().executeScript("window");
                ventana.setMember("javaBridge", puente);
                webView.getEngine().executeScript("window.configurarCentro("
                        + Configuracion.latitudCentroMapa() + ","
                        + Configuracion.longitudCentroMapa() + ","
                        + Configuracion.zoomMapa() + ")");
                enviarSeleccionPendiente();
                enviarDibujoPendiente();
            }
        });
        webView.getEngine().load(recurso.toExternalForm());
    }

    public void configurarSeleccion(BiConsumer<String, Ubicacion> receptor) {
        this.receptor = receptor;
    }

    public void activarSeleccion(String tipo) {
        seleccionPendiente = tipo;
        if (paginaLista) {
            enviarSeleccionPendiente();
        }
    }

    public void mostrar(Ubicacion conductor, Ubicacion origen, Ubicacion destino) {
        dibujoPendiente = "{\"acercamiento\":" + linea(conductor, origen)
                + ",\"traslado\":" + linea(origen, destino)
                + ",\"conductor\":" + punto(conductor)
                + ",\"origen\":" + punto(origen)
                + ",\"destino\":" + punto(destino) + "}";
        if (paginaLista) {
            enviarDibujoPendiente();
        }
    }

    private void recibirPunto(String tipo, Ubicacion ubicacion) {
        if (receptor != null) {
            receptor.accept(tipo, ubicacion);
        }
    }

    private void enviarSeleccionPendiente() {
        if (seleccionPendiente != null) {
            webView.getEngine().executeScript("window.activarSeleccion('"
                    + seleccionPendiente + "')");
            seleccionPendiente = null;
        }
    }

    private void enviarDibujoPendiente() {
        if (dibujoPendiente != null) {
            webView.getEngine().executeScript("window.mostrarRutas("
                    + dibujoPendiente + ")");
            dibujoPendiente = null;
        }
    }

    private static String punto(Ubicacion ubicacion) {
        return "[" + ubicacion.getLatitud() + "," + ubicacion.getLongitud() + "]";
    }

    private static String linea(Ubicacion primero, Ubicacion segundo) {
        return "[" + punto(primero) + "," + punto(segundo) + "]";
    }
}
