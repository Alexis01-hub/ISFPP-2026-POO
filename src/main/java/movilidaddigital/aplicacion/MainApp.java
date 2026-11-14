package movilidaddigital.aplicacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movilidaddigital.interfaz.controller.LoginController;
import movilidaddigital.interfaz.controller.PanelConductorController;
import movilidaddigital.negocio.GestorServicios;
import movilidaddigital.negocio.GestorUsuarios;
import movilidaddigital.negocio.GestorViajes;

public class MainApp extends Application {
    private static GestorServicios gestorServicios;
    private static GestorUsuarios gestorUsuarios;
    private static GestorViajes gestorViajes;

    public static void configurar(GestorServicios servicios, GestorUsuarios usuarios,
                                  GestorViajes viajes) {
        gestorServicios = servicios;
        gestorUsuarios = usuarios;
        gestorViajes = viajes;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/javafx/view/Login.fxml"));
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                return new LoginController(gestorServicios, gestorUsuarios, gestorViajes);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("No se pudo crear el controlador " + type, e);
            }
        });
        Parent root = loader.load();

        primaryStage.setTitle("Sistema de Movilidad Digital");
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(680);
        primaryStage.setScene(new Scene(root, 1100, 760));
        primaryStage.show();
    }
}
