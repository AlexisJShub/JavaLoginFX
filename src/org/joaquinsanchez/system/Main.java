package org.joaquinsanchez.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/org/joaquinsanchez/view/inicioSesionView.fxml"));
        
        // Creamos la escena y le pasamos el FXML cargado
        Scene scene = new Scene(root);
        
        // Configuramos la ventana (Stage)
        primaryStage.setTitle("Sistema - Inicio de Sesión");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); 
        primaryStage.show();
    }

    public static void main(String[] args) {
        
        launch(args);
    }
}