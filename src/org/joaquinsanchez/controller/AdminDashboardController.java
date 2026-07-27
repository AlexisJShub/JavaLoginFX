package org.joaquinsanchez.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.joaquinsanchez.model.Usuario;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label lblBienvenida;

    @FXML
    private Button btnSalir;

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void IniciarUsuario(Usuario usuario) {

        this.usuarioActual = usuario;

        lblBienvenida.setText(
                "Bienvenido administrador "
                + usuario.getUsername()
        );
    }

    @FXML
    private void eventoAbrirUsuarios(ActionEvent evento) {

        try {

            URL archivoFXML = getClass().getResource(
                    "/org/joaquinsanchez/view/UsuarioView.fxml"
            );

            if (archivoFXML == null) {

                System.err.println(
                        "No se encontró la vista UsuarioView.fxml"
                );

                return;
            }

            FXMLLoader cargadorFXML =
                    new FXMLLoader(archivoFXML);

            Parent raiz = cargadorFXML.load();

            Stage escenarioUsuarios = new Stage();

            escenarioUsuarios.setScene(
                    new Scene(raiz)
            );

            escenarioUsuarios.setTitle(
                    "Gestión de usuarios"
            );

            escenarioUsuarios.setResizable(false);
            escenarioUsuarios.centerOnScreen();
            escenarioUsuarios.show();

        } catch (IOException e) {

            System.err.println(
                    "Error al abrir la vista de usuarios: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void eventoSalir(ActionEvent evento) {

        try {

            URL archivoFXML = getClass().getResource(
                    "/org/joaquinsanchez/view/inicioSesionView.fxml"
            );

            if (archivoFXML == null) {

                System.err.println(
                        "No se encontró InicioSesionView.fxml"
                );

                return;
            }

            FXMLLoader cargadorFXML =
                    new FXMLLoader(archivoFXML);

            Parent raiz = cargadorFXML.load();

            Stage escenarioInicio = new Stage();

            escenarioInicio.setScene(
                    new Scene(raiz)
            );

            escenarioInicio.setTitle(
                    "Inicio de sesión"
            );

            escenarioInicio.setResizable(false);
            escenarioInicio.centerOnScreen();
            escenarioInicio.show();

            Stage escenarioActual =
                    (Stage) btnSalir
                            .getScene()
                            .getWindow();

            escenarioActual.close();

        } catch (IOException e) {

            System.err.println(
                    "Error al regresar al inicio de sesión: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}