package movilidaddigital.interfaz.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movilidaddigital.interfaz.mapa.VisualizadorRutasLeaflet;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.CalificacionViaje;
import movilidaddigital.modelo.CategoriaVehiculo;
import movilidaddigital.modelo.EstadoConductor;
import movilidaddigital.modelo.EstadoViaje;
import movilidaddigital.modelo.TipoServicio;
import movilidaddigital.modelo.TipoVehiculo;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.time.LocalDateTime;
public final class PanelClienteController {
    @FXML private Label usuarioLabel;
    @FXML private ComboBox<TipoServicio> tipoServicioCombo;
    @FXML private ComboBox<CategoriaVehiculo> categoriaCombo;
    @FXML private ComboBox<TipoVehiculo> tipoVehiculoCombo;
    @FXML private ToggleButton mapaToggle;
    @FXML private Button cotizarButton;
    @FXML private Label resultadoLabel;
    @FXML private Label estadoLabel;
    @FXML private WebView mapaWeb;
    @FXML private ListView<Viaje> viajesList;
    @FXML private ComboBox<Integer> estrellasConductorCombo;
    @FXML private Label calificacionLabel;

    private final Usuario usuario;
    private final GestorServicios gestorServicios;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;
    private VisualizadorRutasLeaflet visualizador;
    private Alternativa alternativa;
    private Ubicacion origenSeleccionado;
    private Ubicacion destinoSeleccionado;

    public PanelClienteController(Usuario usuario, GestorServicios gestorServicios,
                                  GestorUsuarios gestorUsuarios, GestorViajes gestorViajes) {
        this.usuario = usuario;
        this.gestorServicios = gestorServicios;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @FXML
    private void initialize() {
        usuarioLabel.setText("Cliente: " + usuario.getNombre());
        tipoServicioCombo.setItems(FXCollections.observableArrayList(TipoServicio.values()));
        tipoServicioCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(TipoServicio tipo) {
                return tipo == null ? "" : tipo == TipoServicio.PASAJEROS
                        ? "Traslado de personas" : "Traslado de carga";
            }

            @Override
            public TipoServicio fromString(String texto) {
                return tipoServicioCombo.getValue();
            }
        });
        tipoServicioCombo.getSelectionModel().selectFirst();

        categoriaCombo.setItems(FXCollections.observableArrayList(CategoriaVehiculo.values()));
        categoriaCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(CategoriaVehiculo categoria) {
                return categoria == null ? "" : switch (categoria) {
                    case ESTANDAR -> "Estándar";
                    case CONFORT -> "Confort";
                    case PREMIUM -> "Premium";
                };
            }

            @Override
            public CategoriaVehiculo fromString(String texto) {
                return categoriaCombo.getValue();
            }
        });
        categoriaCombo.getSelectionModel().selectFirst();

        tipoVehiculoCombo.setItems(FXCollections.observableArrayList(TipoVehiculo.values()));
        tipoVehiculoCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(TipoVehiculo tipo) {
                return tipo == null ? "" : tipo == TipoVehiculo.AUTO ? "Auto" : "Moto";
            }

            @Override
            public TipoVehiculo fromString(String texto) {
                return tipoVehiculoCombo.getValue();
            }
        });
        tipoVehiculoCombo.getSelectionModel().selectFirst();
        visualizador = new VisualizadorRutasLeaflet(mapaWeb);
        visualizador.configurarSeleccion(this::recibirPuntoMapa);
        estrellasConductorCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        actualizarViajes();
    }

    @FXML
    private void cambiarModoMapa() {
        limpiarCotizacion();
        if (mapaToggle.isSelected()) {
            origenSeleccionado = null;
            destinoSeleccionado = null;
            estadoLabel.setText("Hacé clic en el mapa para marcar el origen.");
            visualizador.activarSeleccion("origen");
        } else {
            estadoLabel.setText("Modo de selección por mapa desactivado.");
        }
    }

    @FXML
    private void cotizar() {
        if (alternativa != null) {
            solicitarViaje();
            return;
        }
        try {
            if (origenSeleccionado == null || destinoSeleccionado == null) {
                throw new IllegalArgumentException(
                        "Seleccioná el origen y el destino en el mapa.");
            }
            Ubicacion origen = origenSeleccionado;
            Ubicacion destino = destinoSeleccionado;
            TipoServicio tipoServicio = tipoServicioCombo.getValue();
            CategoriaVehiculo categoria = categoriaCombo.getValue();
            TipoVehiculo tipoVehiculo = tipoVehiculoCombo.getValue();
            if (tipoServicio == null || categoria == null || tipoVehiculo == null) {
                throw new IllegalArgumentException(
                        "Elegí el tipo de servicio, la categoría y el tipo de vehículo.");
            }
            var servicio = gestorServicios.getServicios().stream()
                    .filter(s -> s.getTipoServicio() == tipoServicio
                            && s.getCategoriaVehiculo() == categoria
                            && s.getTipoVehiculo() == tipoVehiculo)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No hay un servicio disponible para esa selección."));
            var alternativas = gestorServicios.obtenerAlternativas(origen, destino,
                    tipoServicio, categoria);
            alternativa = alternativas.stream()
                    .filter(a -> a.getServicio() == servicio).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("El servicio no está disponible."));
            resultadoLabel.setText(String.format("Distancia: %.2f km | Tiempo: %.0f min | Total estimado: $%.2f",
                    alternativa.getDistanciaKm(), alternativa.getTiempoMinutos(), alternativa.getCosto()));
            visualizador.mostrar(origen, origen, destino);
            estadoLabel.setText("Cotización lista. Podés solicitar el viaje.");
            cotizarButton.setText("Solicitar viaje");
        } catch (RuntimeException e) {
            error(e.getMessage());
        }
    }

    @FXML
    private void solicitarViaje() {
        if (alternativa == null) {
            cotizar();
            return;
        }
        try {
            Viaje viaje = usuario.getCliente().getViajes().stream()
                    .filter(v -> v.estadoActual() == EstadoViaje.SOLICITADO)
                    .findFirst()
                    .orElseGet(() -> gestorViajes.solicitar(
                            usuario, alternativa, LocalDateTime.now()));
            abrirSeguimiento(viaje);
            cotizarButton.setDisable(true);
            actualizarViajes();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private void abrirSeguimiento(Viaje viaje) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ui/javafx/view/PanelViaje.fxml"));
            loader.setControllerFactory(type -> new PanelViajeController(
                    usuario, viaje, gestorUsuarios, gestorViajes));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Seguimiento del viaje");
            stage.setScene(new Scene(root, 760, 560));
            stage.show();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir el seguimiento del viaje", e);
        }
    }

    @FXML
    private void actualizarViajes() {
        var viajes = FXCollections.<Viaje>observableArrayList();
        for (Viaje viaje : usuario.getCliente().getViajes()) {
            if (!viajes.contains(viaje)) {
                viajes.add(viaje);
            }
        }
        viajesList.setItems(viajes);
        viajesList.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Viaje viaje, boolean vacio) {
                super.updateItem(viaje, vacio);
                setText(vacio || viaje == null ? null : viaje.estadoActual()
                        + " | " + (viaje.getConductor() == null
                        ? "Sin conductor" : viaje.getConductor().getNombre())
                        + (gestorViajes.getCalificacionConductor(viaje).getValor() == 0
                        ? " | Pendiente de calificar" : " | Conductor: "
                        + "★".repeat(gestorViajes.getCalificacionConductor(viaje).getValor())));
            }
        });
    }

    @FXML
    private void calificarConductor() {
        Viaje viaje = viajesList.getSelectionModel().getSelectedItem();
        Integer estrellas = estrellasConductorCombo.getValue();
        if (viaje == null) {
            calificacionLabel.setText("Seleccioná un viaje de la lista.");
            return;
        }
        if (viaje.getConductor() == null
                || viaje.estadoActual() != EstadoViaje.FINALIZADO) {
            calificacionLabel.setText("Solo podés calificar viajes finalizados.");
            return;
        }
        if (estrellas == null) {
            calificacionLabel.setText("Seleccioná una calificación de 1 a 5 estrellas.");
            return;
        }
        try {
            gestorViajes.calificarConductor(viaje, convertirCalificacion(estrellas));
            calificacionLabel.setText("Calificación guardada.");
            estrellasConductorCombo.getSelectionModel().clearSelection();
            actualizarViajes();
        } catch (RuntimeException e) {
            calificacionLabel.setText(e.getMessage());
        }
    }

    private static CalificacionViaje convertirCalificacion(int estrellas) {
        return switch (estrellas) {
            case 1 -> CalificacionViaje.MALO;
            case 2 -> CalificacionViaje.REGULAR;
            case 3 -> CalificacionViaje.BUENO;
            case 4 -> CalificacionViaje.MUY_BUENO;
            case 5 -> CalificacionViaje.EXCELENTE;
            default -> throw new IllegalArgumentException(
                    "La calificación debe tener entre 1 y 5 estrellas.");
        };
    }

    @FXML
    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ui/javafx/view/Login.fxml"));
            loader.setControllerFactory(type -> type == LoginController.class
                    ? new LoginController(gestorServicios, gestorUsuarios, gestorViajes)
                    : crearControlador(type));
            Parent root = loader.load();
            Stage stage = (Stage) viajesList.getScene().getWindow();
            stage.setScene(new Scene(root, 520, 430));
            stage.setTitle("Movilidad Digital");
        } catch (Exception e) {
            error("No se pudo volver al login: " + e.getMessage());
        }
    }

    private Object crearControlador(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear el controlador " + type, e);
        }
    }

    private void recibirPuntoMapa(String tipo, Ubicacion ubicacion) {
        if (!mapaToggle.isSelected()) return;
        if ("origen".equals(tipo)) {
            origenSeleccionado = ubicacion;
            estadoLabel.setText("Origen marcado. Hacé clic para marcar el destino.");
            visualizador.activarSeleccion("destino");
        } else {
            destinoSeleccionado = ubicacion;
            estadoLabel.setText("Destino marcado. Ya podés cotizar.");
            limpiarCotizacion();
            mapaToggle.setSelected(false);
        }
    }

    private void limpiarCotizacion() {
        alternativa = null;
        resultadoLabel.setText("Sin cotización.");
        cotizarButton.setText("Cotizar viaje");
        cotizarButton.setDisable(false);
    }

    private void error(String mensaje) {
        resultadoLabel.setText("Error: " + mensaje);
        estadoLabel.setText("Revisá los datos ingresados.");
    }
}
