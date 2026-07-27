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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.joaquinsanchez.dao.UsuarioDAO;
import org.joaquinsanchez.util.SecurityUtil;

public class RegistroController implements Initializable {

    @FXML
    private ToggleButton btnRolAdmin;

    @FXML
    private ToggleButton btnRolEmpleado;

    @FXML
    private ToggleButton btnRolCajero;

    @FXML
    private TextField txtNuevoUsuario;

    @FXML
    private PasswordField txtNuevaPassword;

    @FXML
    private PasswordField txtConfirmarPassword;

    @FXML
    private Label lblMensajeRegistro;

    @FXML
    private Hyperlink lnkVolverLogin;

    private ToggleGroup grupoRoles;

    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuarioDAO = new UsuarioDAO();

        // Agrupar los botones de rol para que solo uno esté activo a la vez
        grupoRoles = new ToggleGroup();

        btnRolAdmin.setToggleGroup(grupoRoles);
        btnRolEmpleado.setToggleGroup(grupoRoles);
        btnRolCajero.setToggleGroup(grupoRoles);

        btnRolEmpleado.setSelected(true);

        lblMensajeRegistro.setText("");
    }

    private String obtenerRolSeleccionado() {

        if (btnRolAdmin.isSelected()) {
            return "admin";
        } else if (btnRolEmpleado.isSelected()) {
            return "empleado";
        } else if (btnRolCajero.isSelected()) {
            return "cajero";
        }

        return null;
    }

    @FXML
    private void eventoRegistrarCuenta(ActionEvent evento) {

        String username = txtNuevoUsuario.getText().trim();
        String password = txtNuevaPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();
        String rol = obtenerRolSeleccionado();

        lblMensajeRegistro.setStyle("-fx-text-fill: #c43d3d;");

        if (username.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
            lblMensajeRegistro.setText("Complete todos los campos.");
            return;
        }

        if (rol == null) {
            lblMensajeRegistro.setText("Seleccione un rol para su cuenta.");
            return;
        }

        if (username.length() < 3) {
            lblMensajeRegistro.setText("El usuario debe tener al menos 3 caracteres.");
            return;
        }

        if (password.length() < 4) {
            lblMensajeRegistro.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            lblMensajeRegistro.setText("Las contraseñas no coinciden.");
            return;
        }

        String passwordHash = SecurityUtil.hashSHA256(password);

        boolean registrado = usuarioDAO.registrarUsuario(username, passwordHash, rol);

        if (registrado) {

            lblMensajeRegistro.setStyle("-fx-text-fill: #2f8a53;");
            lblMensajeRegistro.setText(
                    "Cuenta de " + rol + " creada correctamente. Ya puedes iniciar sesión."
            );

            limpiarCampos();

        } else {

            lblMensajeRegistro.setText(
                    "No se pudo crear la cuenta. Verifique que el usuario no exista."
            );
        }
    }

    private void limpiarCampos() {
        txtNuevoUsuario.clear();
        txtNuevaPassword.clear();
        txtConfirmarPassword.clear();
        btnRolEmpleado.setSelected(true);
    }

    @FXML
    private void eventoVolverLogin(ActionEvent evento) {

        try {

            URL archivoFXML =
                    getClass().getResource("/org/joaquinsanchez/view/inicioSesionView.fxml");

            Parent raiz =
                    FXMLLoader.load(archivoFXML);

            Stage escenaActual =
                    (Stage) lnkVolverLogin.getScene().getWindow();

            escenaActual.setScene(new Scene(raiz));
            escenaActual.setTitle("Iniciar sesión - JavaLogin");
            escenaActual.centerOnScreen();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar la vista de inicio de sesión: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}