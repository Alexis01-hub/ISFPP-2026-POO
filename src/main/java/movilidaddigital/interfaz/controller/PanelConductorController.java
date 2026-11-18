package movilidaddigital.interfaz.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import movilidaddigital.datos.Configuracion;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.Viaje;
import movilidaddigital.modelo.Ubicacion;
import movilidaddigital.modelo.CategoriaVehiculo;
import movilidaddigital.modelo.CalificacionViaje;
import movilidaddigital.modelo.EstadoConductor;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PanelConductorController {
    @FXML private Label usuarioLabel;
    @FXML private ComboBox<Vehiculo> vehiculoCombo;
    @FXML private ComboBox<CategoriaVehiculo> categoriaCombo;
    @FXML private Label estadoLabel;
    @FXML private ListView<Viaje> solicitudesList;
    @FXML private Button disponibilidadButton;
    @FXML private Label mensajeLabel;

    private final Usuario usuario;
    private final GestorServicios gestorServicios;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;

    public PanelConductorController(Usuario usuario, GestorServicios gestorServicios,
                                    GestorUsuarios gestorUsuarios,
                                    GestorViajes gestorViajes) {
        this.usuario = usuario;
        this.gestorServicios = gestorServicios;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @FXML
    private void initialize() {
        usuarioLabel.setText("Conductor: " + usuario.getNombre());
        vehiculoCombo.setItems(FXCollections.observableArrayList(
                usuario.getConductor().getVehiculos()));
        vehiculoCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Vehiculo vehiculo) {
                return vehiculo == null ? "" : vehiculo.getModelo() + " - "
                        + vehiculo.getPatente();
            }

            @Override
            public Vehiculo fromString(String texto) {
                return vehiculoCombo.getValue();
            }
        });
        categoriaCombo.setItems(FXCollections.observableArrayList(CategoriaVehiculo.values()));
        categoriaCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(CategoriaVehiculo categoria) {
                return categoria == null ? "" : categoria.name();
            }

            @Override
            public CategoriaVehiculo fromString(String texto) {
                return categoriaCombo.getValue();
            }
        });
        vehiculoCombo.getSelectionModel().select(usuario.getConductor().getVehiculoActivo());
        categoriaCombo.getSelectionModel().select(usuario.getConductor().getCategoriaVehiculoActivo());
        actualizarEstado();
        generarSolicitudesSimuladas();
        actualizarSolicitudes();
    }

    @FXML
    private void guardarConfiguracion() {
        try {
            gestorUsuarios.seleccionarVehiculo(usuario, vehiculoCombo.getValue(),
                    categoriaCombo.getValue());
            mensajeLabel.setText("Configuración guardada.");
            actualizarEstado();
        } catch (RuntimeException e) {
            mensajeLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void cambiarDisponibilidad() {
        try {
            if (usuario.getConductor().getEstadoConductor() == EstadoConductor.DISPONIBLE) {
                gestorUsuarios.ponerFueraDeServicio(usuario);
            } else {
                gestorUsuarios.ponerDisponible(usuario);
            }
            actualizarEstado();
            actualizarSolicitudes();
        } catch (RuntimeException e) {
            mensajeLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void actualizarSolicitudes() {
        var pendientes = FXCollections.<Viaje>observableArrayList();
        Despachador despachador = new Despachador(gestorUsuarios);
        for (Viaje viaje : gestorViajes.getViajes()) {
            boolean pendienteCompatible = viaje.estadoActual()
                    == movilidaddigital.modelo.EstadoViaje.SOLICITADO
                    && despachador.esCompatible(usuario.getConductor(), viaje.getServicio());
            boolean propioFinalizado = viaje.getConductor() == usuario
                    && viaje.estadoActual() == movilidaddigital.modelo.EstadoViaje.FINALIZADO;
            if (pendienteCompatible || propioFinalizado) {
                pendientes.add(viaje);
            }
        }
        solicitudesList.setItems(pendientes);
        solicitudesList.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Viaje viaje, boolean vacio) {
                super.updateItem(viaje, vacio);
                setText(vacio || viaje == null ? null : viaje.getCliente().getNombre()
                        + " | " + viaje.getServicio().getNombre()
                        + (viaje.getCalificacionCliente().getValor() == 0
                        ? " | Pendiente de calificar" : " | Cliente: "
                        + "★".repeat(viaje.getCalificacionCliente().getValor())));
            }
        });
    }

    @FXML
    private void generarSolicitudesSimuladas() {
        Despachador despachador = new Despachador(gestorUsuarios);
        var serviciosCompatibles = gestorServiciosDisponibles(despachador);
        if (serviciosCompatibles.isEmpty()) {
            mensajeLabel.setText("No hay servicios compatibles con el vehículo activo.");
            return;
        }

        List<Usuario> clientes = new ArrayList<>();
        for (Usuario candidato : gestorUsuarios.getUsuarios()) {
            if (candidato != usuario && candidato.getCliente() != null
                    && !candidato.getCliente().enViaje()) {
                clientes.add(candidato);
            }
        }

        int creadas = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Usuario cliente : clientes) {
            if (creadas == 3) {
                break;
            }
            try {
                Ubicacion origen = ubicacionAleatoria(random);
                Ubicacion destino = ubicacionAleatoria(random);
                var servicio = serviciosCompatibles.get(creadas % serviciosCompatibles.size());
                var alternativas = gestorServicios.obtenerAlternativas(origen, destino,
                        servicio.getTipoServicio(), servicio.getCategoriaVehiculo());
                Alternativa alternativa = alternativas.stream()
                        .filter(a -> a.getServicio() == servicio)
                        .findFirst()
                        .orElse(null);
                if (alternativa != null) {
                    gestorViajes.solicitar(cliente, alternativa, LocalDateTime.now());
                    creadas++;
                }
            } catch (RuntimeException ignorada) {
                // Una solicitud que no puede crearse no impide simular las siguientes.
            }
        }
        mensajeLabel.setText(creadas == 0
                ? "No se pudieron crear solicitudes simuladas."
                : "Se generaron " + creadas + " solicitudes simuladas.");
        actualizarSolicitudes();
    }

    private List<movilidaddigital.modelo.Servicio> gestorServiciosDisponibles(
            Despachador despachador) {
        List<movilidaddigital.modelo.Servicio> compatibles = new ArrayList<>();
        for (var servicio : gestorServicios.getServicios()) {
            if (despachador.esCompatible(usuario.getConductor(), servicio)) {
                compatibles.add(servicio);
            }
        }
        return compatibles;
    }

    private static Ubicacion ubicacionAleatoria(ThreadLocalRandom random) {
        return new Ubicacion(
                random.nextDouble(Configuracion.latitudMinima(), Configuracion.latitudMaxima()),
                random.nextDouble(Configuracion.longitudMinima(), Configuracion.longitudMaxima()));
    }

    @FXML
    private void aceptarSolicitud() {
        Viaje viaje = solicitudesList.getSelectionModel().getSelectedItem();
        if (viaje == null) {
            mensajeLabel.setText("Seleccioná una solicitud.");
            return;
        }
        try {
            gestorViajes.aceptar(viaje, usuario, LocalDateTime.now());
            gestorViajes.iniciar(viaje, LocalDateTime.now());
            gestorViajes.finalizar(viaje, LocalDateTime.now(),
                    CalificacionViaje.NO_CALIFICADO, CalificacionViaje.NO_CALIFICADO);
            mensajeLabel.setText("Viaje finalizado. Elegí la calificación del cliente.");
            actualizarSolicitudes();
            actualizarEstado();
        } catch (RuntimeException e) {
            mensajeLabel.setText(e.getMessage());
        }
    }

    private void actualizarEstado() {
        EstadoConductor estado = usuario.getConductor().getEstadoConductor();
        estadoLabel.setText("Estado: " + estado);
        disponibilidadButton.setText(estado == EstadoConductor.DISPONIBLE
                ? "Pasar a fuera de servicio" : "Ponerse disponible");
    }

    @FXML
    private void volverAlLogin() {
        try {
            gestorUsuarios.ponerFueraDeServicio(usuario);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ui/javafx/view/Login.fxml"));
            loader.setControllerFactory(type -> type == LoginController.class
                    ? new LoginController(gestorServicios, gestorUsuarios, gestorViajes)
                    : crearControlador(type));
            Parent root = loader.load();
            Stage stage = (Stage) solicitudesList.getScene().getWindow();
            stage.setScene(new Scene(root, 520, 430));
            stage.setTitle("Movilidad Digital");
        } catch (Exception e) {
            mensajeLabel.setText("No se pudo volver al login: " + e.getMessage());
        }
    }

    private Object crearControlador(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear el controlador " + type, e);
        }
    }
}
