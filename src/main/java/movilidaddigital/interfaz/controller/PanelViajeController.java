package movilidaddigital.interfaz.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import movilidaddigital.modelo.*;
import movilidaddigital.negocio.Despachador;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

import java.time.LocalDateTime;

/**
 * Permite seleccionar conductor y avanzar o cancelar un viaje paso a paso.
 */
public final class PanelViajeController {
    @FXML private Label viajeLabel;
    @FXML private ComboBox<Usuario> conductorCombo;
    @FXML private ListView<String> historialList;
    @FXML private Button avanzarButton;
    @FXML private Button cancelarButton;
    @FXML private Label estadoLabel;

    private final Usuario cliente;
    private final Viaje viaje;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;

    public PanelViajeController(Usuario cliente, Viaje viaje,
                                GestorUsuarios gestorUsuarios, GestorViajes gestorViajes) {
        this.cliente = cliente;
        this.viaje = viaje;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @FXML
    private void initialize() {
        viajeLabel.setText("Viaje " + viaje.getId());
        cargarConductores();
        actualizarHistorial();
    }

    private void cargarConductores() {
        var elegibles = new java.util.ArrayList<Usuario>();
        Despachador despachador = new Despachador(gestorUsuarios);
        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario == cliente || usuario.getConductor() == null) {
                continue;
            }
            if (usuario.getRolActivo() == RolUsuario.CLIENTE
                    && usuario.getConductor().getEstadoConductor() == EstadoConductor.FUERA_DE_SERVICIO) {
                usuario.cambiarRolActivo(RolUsuario.CONDUCTOR);
                gestorUsuarios.ponerDisponible(usuario);
            }
            if (usuario.getRolActivo() == RolUsuario.CONDUCTOR
                    && usuario.getConductor().getEstadoConductor() == EstadoConductor.DISPONIBLE
                    && despachador.esCompatible(usuario.getConductor(), viaje.getServicio())) {
                elegibles.add(usuario);
            }
        }
        conductorCombo.setItems(FXCollections.observableArrayList(elegibles));
        conductorCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getNombre() + " | "
                        + usuario.getConductor().getVehiculoActivo().getModelo()
                        + " | " + usuario.getConductor().getVehiculoActivo().getCategoriaVehiculo();
            }

            @Override
            public Usuario fromString(String texto) {
                return conductorCombo.getValue();
            }
        });
    }

    @FXML
    private void avanzar() {
        try {
            switch (viaje.estadoActual()) {
                case SOLICITADO -> {
                    Usuario conductor = conductorCombo.getValue();
                    if (conductor == null) {
                        throw new IllegalArgumentException("Seleccioná un conductor.");
                    }
                    gestorViajes.aceptar(viaje, conductor, LocalDateTime.now());
                }
                case ACEPTADO -> gestorViajes.iniciar(viaje, LocalDateTime.now());
                case INICIADO -> gestorViajes.finalizar(viaje, LocalDateTime.now(),
                        CalificacionViaje.NO_CALIFICADO, CalificacionViaje.NO_CALIFICADO);
                default -> throw new IllegalStateException("El viaje no puede avanzar desde "
                        + viaje.estadoActual());
            }
            actualizarHistorial();
        } catch (RuntimeException e) {
            estadoLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        if (viaje.estadoActual() == EstadoViaje.FINALIZADO) {
            estadoLabel.setText("Un viaje finalizado no se puede cancelar.");
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("Cancelación solicitada por el cliente");
        dialogo.setTitle("Cancelar viaje");
        dialogo.setHeaderText("Ingresá el motivo de cancelación");
        dialogo.showAndWait().ifPresent(motivo -> {
            try {
                double costo = gestorViajes.cancelar(viaje, cliente, motivo, LocalDateTime.now());
                estadoLabel.setText(String.format("Viaje cancelado. Costo: $%.2f", costo));
                actualizarHistorial();
            } catch (RuntimeException e) {
                estadoLabel.setText(e.getMessage());
            }
        });
    }

    private void actualizarHistorial() {
        var registros = FXCollections.<String>observableArrayList();
        for (RegistroViaje registro : viaje.getRegistroViaje()) {
            registros.add("%-15s -> [%s]".formatted(
                    registro.getEstadoViaje(), registro.getFechaHora()));
        }
        historialList.setItems(registros);
        estadoLabel.setText("Estado actual: " + viaje.estadoActual());
        avanzarButton.setDisable(viaje.estadoActual() == EstadoViaje.FINALIZADO
                || viaje.estadoActual() == EstadoViaje.CANCELADO);
        cancelarButton.setDisable(viaje.estadoActual() == EstadoViaje.FINALIZADO
                || viaje.estadoActual() == EstadoViaje.CANCELADO);
        conductorCombo.setDisable(viaje.estadoActual() != EstadoViaje.SOLICITADO);
    }
}
