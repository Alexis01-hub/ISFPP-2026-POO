package movilidaddigital.aplicacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga la vista principal desde resources/vistas
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/MenuPrincipalView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Sistema de Movilidad Digital");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
