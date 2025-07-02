package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            System.out.println("Iniciando aplicacion...");

            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/hello-view.fxml"));
            Parent root = loader.load();

            // Crear escena
            Scene scene = new Scene(root, 1200, 800);

            // Configurar ventana
            stage.setTitle("Sistema de Evaluacion - UNMSM");
            stage.setScene(scene);
            stage.show();

            System.out.println("Aplicacion iniciada correctamente!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}