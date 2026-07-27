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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.joaquinsanchez.dao.UsuarioDAO;
import org.joaquinsanchez.model.Usuario;
import org.joaquinsanchez.util.SecurityUtil;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btniniciarSesion;

    @FXML
    private Label lblMensaje;

    @FXML
    private Hyperlink lnkRegistro;

    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        lblMensaje.setText("");
    }

    @FXML
    public void eventoInicioSesion(ActionEvent evento) {

        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Color de error por defecto
        lblMensaje.setStyle("-fx-text-fill: #c43d3d;");

        // Verificar si los campos están vacíos
        if (usuario.isEmpty() || password.isEmpty()) {

            lblMensaje.setText(
                    "Por favor, complete todos sus datos."
            );

            return;
        }

        // Convertir la contraseña a SHA-256
        String passwordHash =
                SecurityUtil.hashSHA256(password);

        // Intentar iniciar sesión
        Usuario usuarioIniciado =
                usuarioDAO.iniciarSesion(
                        usuario,
                        passwordHash
                );

        if (usuarioIniciado != null) {

            lblMensaje.setStyle(
                    "-fx-text-fill: #2f8a53;"
            );

            lblMensaje.setText(
                    "Inicio correcto"
            );

            abrirDashboard(usuarioIniciado);

        } else {

            lblMensaje.setText(
                    "Usuario o contraseña incorrectos"
            );
        }
    }

    private void abrirDashboard(Usuario usuario) {

        String rutaFXML;
        String tituloDashboard;

        switch (usuario.getRol().toLowerCase()) {

            case "admin":
                rutaFXML =
                        "/org/joaquinsanchez/view/AdminDashboardView.fxml";

                tituloDashboard =
                        "Panel de Administración";
                break;

            case "empleado":
                rutaFXML =
                        "/org/joaquinsanchez/view/EmpleadoDashboardView.fxml";

                tituloDashboard =
                        "Panel de Empleado";
                break;

            case "cajero":
                rutaFXML =
                        "/org/joaquinsanchez/view/CajeroDashboardView.fxml";

                tituloDashboard =
                        "Panel de Cajero";
                break;

            default:
                lblMensaje.setStyle(
                        "-fx-text-fill: #c43d3d;"
                );

                lblMensaje.setText(
                        "El rol del usuario no es válido."
                );

                return;
        }

        try {

            URL archivoFXML =
                    getClass().getResource(rutaFXML);

            if (archivoFXML == null) {

                lblMensaje.setStyle(
                        "-fx-text-fill: #c43d3d;"
                );

                lblMensaje.setText(
                        "No se encontró la vista: "
                        + rutaFXML
                );

                return;
            }

            FXMLLoader cargadorFXML =
                    new FXMLLoader(archivoFXML);

            Parent raiz =
                    cargadorFXML.load();

            switch (usuario.getRol().toLowerCase()) {

                case "admin":

                    AdminDashboardController adminController =
                            cargadorFXML.getController();

                    adminController.IniciarUsuario(usuario);
                    break;

                case "empleado":

                    EmpleadoDashboardController empleadoController =
                            cargadorFXML.getController();

                    empleadoController.iniciarUsuario(usuario);
                    break;

                case "cajero":

                    CajeroDashboardController cajeroController =
                            cargadorFXML.getController();

                    cajeroController.iniciarUsuario(usuario);
                    break;

                default:
                    return;
            }

            Stage escenario =
                    new Stage();

            escenario.setScene(
                    new Scene(raiz)
            );

            escenario.setTitle(
                    tituloDashboard
            );

            escenario.setResizable(false);
            escenario.centerOnScreen();
            escenario.show();

            Stage escenaActual =
                    (Stage) btniniciarSesion
                            .getScene()
                            .getWindow();

            escenaActual.close();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar la vista: "
                    + rutaFXML
                    + " - "
                    + e.getMessage()
            );

            e.printStackTrace();

            lblMensaje.setStyle(
                    "-fx-text-fill: #c43d3d;"
            );

            lblMensaje.setText(
                    "Error interno al cargar el panel."
            );
        }
    }

    @FXML
    private void eventoAbrirRegistro(ActionEvent evento) {

        try {

            URL archivoFXML =
                    getClass().getResource("/org/joaquinsanchez/view/RegistroView.fxml");

            Parent raiz =
                    FXMLLoader.load(archivoFXML);

            Stage escenaActual =
                    (Stage) lnkRegistro.getScene().getWindow();

            escenaActual.setScene(new Scene(raiz));
            escenaActual.setTitle("Crear cuenta - JavaLogin");
            escenaActual.centerOnScreen();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar la vista de registro: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}