package org.joaquinsanchez.controller;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.joaquinsanchez.dao.UsuarioDAO;
import org.joaquinsanchez.model.Usuario;
import org.joaquinsanchez.util.SecurityUtil;

public class UsuarioController implements Initializable {

    @FXML
    private TextField txtNuevoUsuario;

    @FXML
    private PasswordField txtNuevaPassword;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private Label lblMensajeUsuario;

    @FXML
    private TableView<Usuario> tblUsuarios;

    @FXML
    private TableColumn<Usuario, Integer> colId;

    @FXML
    private TableColumn<Usuario, String> colUsername;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableColumn<Usuario, Boolean> colActivo;

    @FXML
    private TableColumn<Usuario, Timestamp> colFechaCreacion;

    private UsuarioDAO usuarioDAO;

    private ObservableList<Usuario> listaUsuarios;

    // Usuario seleccionado actualmente
    private Usuario usuarioSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuarioDAO = new UsuarioDAO();

        listaUsuarios = FXCollections.observableArrayList();

        cmbRol.setItems(FXCollections.observableArrayList("admin", "empleado", "cajero"));

        configurarTabla();

        cargarUsuarios();

        // Detectar cuando el administrador selecciona un usuario
        configurarSeleccionUsuario();

        lblMensajeUsuario.setText("");
    }

    private void configurarTabla() {

        colId.setCellValueFactory(dato -> new SimpleIntegerProperty(dato.getValue().getId()).asObject());

        colUsername.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getUsername()));

        colRol.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getRol()));

        colActivo.setCellValueFactory(dato -> new SimpleBooleanProperty(dato.getValue().isActivo()));

        colFechaCreacion.setCellValueFactory(dato -> new SimpleObjectProperty<>(dato.getValue().getFechaCreacion()));
    }

    private void cargarUsuarios() {

        listaUsuarios.clear();

        listaUsuarios.addAll(usuarioDAO.listarUsuarios());

        tblUsuarios.setItems(listaUsuarios);
    }

    // Configurar selección de usuarios
    private void configurarSeleccionUsuario() {

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, usuarioAnterior, usuarioNuevo) -> {

            if (usuarioNuevo != null) {
                usuarioSeleccionado = usuarioNuevo;

                lblMensajeUsuario.setStyle("-fx-text-fill: #2f8a53;");
                lblMensajeUsuario.setText("Usuario seleccionado: " + usuarioNuevo.getUsername());
            }
        });
    }

    @FXML
    private void eventoRegistrarUsuario(ActionEvent evento) {

        String username = txtNuevoUsuario.getText().trim();
        String password = txtNuevaPassword.getText();
        String rol = cmbRol.getValue();

        lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");

        if (username.isEmpty() || password.isEmpty() || rol == null) {
            lblMensajeUsuario.setText("Complete todos los campos.");
            return;
        }

        if (username.length() < 3) {
            lblMensajeUsuario.setText("El usuario debe tener al menos 3 caracteres.");
            return;
        }

        if (password.length() < 4) {
            lblMensajeUsuario.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        String passwordHash = SecurityUtil.hashSHA256(password);

        boolean registrado = usuarioDAO.registrarUsuario(username, passwordHash, rol);

        if (registrado) {
            lblMensajeUsuario.setStyle("-fx-text-fill: #2f8a53;");
            lblMensajeUsuario.setText("Usuario registrado correctamente.");

            limpiarCampos();
            cargarUsuarios();

        } else {
            lblMensajeUsuario.setText("No se pudo registrar el usuario.");
        }
    }

    private void limpiarCampos() {
        txtNuevoUsuario.clear();
        txtNuevaPassword.clear();
        cmbRol.getSelectionModel().clearSelection();
    }

    // Método para obtener el usuario seleccionado
    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }
    
    @FXML
    private void eventoCambiarEstadoUsuario(ActionEvent evento) {

        // Verificar si hay un usuario seleccionado
        if (usuarioSeleccionado == null) {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("Seleccione un usuario de la tabla.");
            return;
        }

        // Obtener el estado actual
        boolean estadoActual = usuarioSeleccionado.isActivo();

        // Cambiar el estado
        boolean nuevoEstado = !estadoActual;

        // Actualizar en la base de datos
        boolean actualizado = usuarioDAO.cambiarEstadoUsuario(usuarioSeleccionado.getId(), nuevoEstado);

        if (actualizado) {
            // Actualizar el objeto seleccionado
            usuarioSeleccionado.setActivo(nuevoEstado);

            // Recargar la tabla
            cargarUsuarios();

            // Mostrar mensaje
            lblMensajeUsuario.setStyle("-fx-text-fill: #2f8a53;");

            if (nuevoEstado) {
                lblMensajeUsuario.setText("Usuario activado correctamente.");
            } else {
                lblMensajeUsuario.setText("Usuario desactivado correctamente.");
            }

        } else {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("No se pudo cambiar el estado del usuario.");
        }
    }

    @FXML
    private void eventoCambiarRolUsuario(ActionEvent evento) {

        // Verificar si hay un usuario seleccionado
        if (usuarioSeleccionado == null) {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("Seleccione un usuario de la tabla.");
            return;
        }

        // Obtener el nuevo rol seleccionado
        String nuevoRol = cmbRol.getValue();

        // Verificar que se haya seleccionado un rol
        if (nuevoRol == null || nuevoRol.isEmpty()) {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("Seleccione el nuevo rol del usuario.");
            return;
        }

        // Evitar cambiar al mismo rol
        if (usuarioSeleccionado.getRol().equalsIgnoreCase(nuevoRol)) {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("El usuario ya tiene este rol.");
            return;
        }

        // Cambiar el rol en la base de datos
        boolean actualizado = usuarioDAO.cambiarRolUsuario(usuarioSeleccionado.getId(), nuevoRol);

        if (actualizado) {
            // Actualizar el objeto seleccionado
            usuarioSeleccionado.setRol(nuevoRol);

            // Recargar la tabla
            cargarUsuarios();

            // Mostrar mensaje de éxito
            lblMensajeUsuario.setStyle("-fx-text-fill: #2f8a53;");
            lblMensajeUsuario.setText("Rol actualizado correctamente.");

            // Limpiar selección del ComboBox
            cmbRol.getSelectionModel().clearSelection();

        } else {
            lblMensajeUsuario.setStyle("-fx-text-fill: #c43d3d;");
            lblMensajeUsuario.setText("No se pudo actualizar el rol del usuario.");
        }
    }
    
    @FXML
private void eventoActualizarUsuarios(ActionEvent evento) {

    cargarUsuarios();

    // Limpiar el usuario seleccionado
    usuarioSeleccionado = null;

    // Limpiar la selección de la tabla
    tblUsuarios.getSelectionModel().clearSelection();

    // Limpiar el ComboBox del rol
    cmbRol.getSelectionModel().clearSelection();

    // Mostrar mensaje
    lblMensajeUsuario.setStyle(
            "-fx-text-fill: #2f8a53;"
    );

    lblMensajeUsuario.setText(
            "Lista de usuarios actualizada correctamente."
    );
}
}