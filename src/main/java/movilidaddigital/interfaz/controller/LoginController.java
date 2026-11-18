package movilidaddigital.interfaz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.RolUsuario;
import movilidaddigital.modelo.EstadoConductor;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

public final class LoginController {
    @FXML private ComboBox<Usuario> usuarioCombo;
    @FXML private ComboBox<RolUsuario> rolCombo;
    @FXML private Label errorLabel;
    private final GestorServicios gestorServicios;
    private final GestorUsuarios gestorUsuarios;
    private final GestorViajes gestorViajes;

    public LoginController(GestorServicios gestorServicios, GestorUsuarios gestorUsuarios,
                           GestorViajes gestorViajes) {
        this.gestorServicios = gestorServicios;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorViajes = gestorViajes;
    }

    @FXML
    private void initialize() {
        usuarioCombo.getItems().setAll(gestorUsuarios.getUsuarios());
        usuarioCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getNombre() + " (" + usuario.getEmail() + ")";
            }

            @Override
            public Usuario fromString(String texto) {
                return usuarioCombo.getValue();
            }
        });
        if (!usuarioCombo.getItems().isEmpty()) {
            usuarioCombo.getSelectionModel().selectFirst();
        } else {
            errorLabel.setText("No hay usuarios cargados.");
        }
        rolCombo.getItems().setAll(RolUsuario.values());
        rolCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(RolUsuario rol) {
                return rol == null ? "" : rol == RolUsuario.CLIENTE
                        ? "Cliente: solicitar viajes" : "Conductor: prestar servicios";
            }

            @Override
            public RolUsuario fromString(String texto) {
                return rolCombo.getValue();
            }
        });
        rolCombo.getSelectionModel().select(RolUsuario.CLIENTE);
        usuarioCombo.valueProperty().addListener((obs, anterior, usuario) -> actualizarRoles(usuario));
        actualizarRoles(usuarioCombo.getValue());
    }

    @FXML
    private void iniciarSesion() {
        Usuario usuario = usuarioCombo.getValue();
        if (usuario == null) {
            errorLabel.setText("Seleccioná un usuario para ingresar.");
            return;
        }
        try {
            RolUsuario rol = rolCombo.getValue();
            if (rol == RolUsuario.CONDUCTOR && usuario.getConductor() == null) {
                errorLabel.setText("Este usuario no está registrado como conductor.");
                return;
            }
            if (rol == RolUsuario.CONDUCTOR
                    && usuario.getConductor().getEstadoConductor() != EstadoConductor.FUERA_DE_SERVICIO) {
                gestorUsuarios.ponerFueraDeServicio(usuario);
            }
            usuario.cambiarRolActivo(rol);
            String vista = rol == RolUsuario.CONDUCTOR
                    ? "/ui/javafx/view/PanelConductor.fxml"
                    : "/ui/javafx/view/PanelCliente.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            loader.setControllerFactory(type -> {
                if (type == PanelClienteController.class) {
                    return new PanelClienteController(usuario, gestorServicios, gestorUsuarios, gestorViajes);
                }
                if (type == PanelConductorController.class) {
                    return new PanelConductorController(usuario, gestorServicios,
                            gestorUsuarios, gestorViajes);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("No se pudo crear el controlador " + type, e);
                }
            });
            Parent root = loader.load();
            Stage stage = (Stage) usuarioCombo.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 760));
            stage.setTitle("Movilidad Digital - Panel de cliente");
        } catch (Exception e) {
            e.printStackTrace();

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            errorLabel.setText("No se pudo abrir el panel: " + causa.getMessage());
        }
    }

    private void actualizarRoles(Usuario usuario) {
        if (usuario == null || usuario.getConductor() == null) {
            rolCombo.getItems().setAll(RolUsuario.CLIENTE);
            rolCombo.getSelectionModel().select(RolUsuario.CLIENTE);
            rolCombo.setDisable(true);
        } else {
            rolCombo.setDisable(false);
            rolCombo.getItems().setAll(RolUsuario.values());
            if (rolCombo.getValue() == null) {
                rolCombo.getSelectionModel().select(RolUsuario.CLIENTE);
            }
        }
    }
}
