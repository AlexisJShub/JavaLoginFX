package org.joaquinsanchez.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.joaquinsanchez.model.Usuario;
import org.joaquinsanchez.util.Conexion;

public class UsuarioDAO {

    // Iniciar sesión
    public Usuario iniciarSesion(String username, String passwordHash) {
        Usuario usuario = null;
        String sql = "{call sp_iniciar_sesion(?, ?)}";

        try (
            Connection conexion = Conexion.getInstancia().conectar();
            CallableStatement consulta = conexion.prepareCall(sql)
        ) {
            consulta.setString(1, username);
            consulta.setString(2, passwordHash);

            try (ResultSet tablaResultado = consulta.executeQuery()) {
                if (tablaResultado.next()) {
                    usuario = new Usuario();
                    usuario.setId(tablaResultado.getInt("id"));
                    usuario.setUsername(tablaResultado.getString("username"));
                    usuario.setRol(tablaResultado.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }

        return usuario;
    }

    // Registrar usuario
    public boolean registrarUsuario(String username, String passwordHash, String rol) {
        String sql = "{call sp_registrar_usuario(?, ?, ?)}";

        try (
            Connection conexion = Conexion.getInstancia().conectar();
            CallableStatement consulta = conexion.prepareCall(sql)
        ) {
            consulta.setString(1, username);
            consulta.setString(2, passwordHash);
            consulta.setString(3, rol.toLowerCase());

            int filasAfectadas = consulta.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Listar usuarios
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "{call sp_listarusuarios()}";

        try (
            Connection conexion = Conexion.getInstancia().conectar();
            CallableStatement consulta = conexion.prepareCall(sql);
            ResultSet tablaResultado = consulta.executeQuery()
        ) {
            while (tablaResultado.next()) {
                Usuario usuario = new Usuario();
                
                usuario.setId(tablaResultado.getInt("id"));
                usuario.setUsername(tablaResultado.getString("username"));
                usuario.setRol(tablaResultado.getString("rol"));
                usuario.setActivo(tablaResultado.getBoolean("activo"));
                usuario.setFechaCreacion(tablaResultado.getTimestamp("fecha_creacion"));
                
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }
    
    public boolean cambiarEstadoUsuario(int id, boolean activo) {
        String sql = "{call sp_cambiar_estado_usuario(?, ?)}";

        try (
            Connection conexion = Conexion.getInstancia().conectar();
            CallableStatement consulta = conexion.prepareCall(sql)
        ) {
            consulta.setInt(1, id);
            consulta.setBoolean(2, activo);

            int filasAfectadas = consulta.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar el estado del usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarRolUsuario(int id, String rol) {
        String sql = "{call sp_cambiar_rol_usuario(?, ?)}";

        try (
            Connection conexion = Conexion.getInstancia().conectar();
            CallableStatement consulta = conexion.prepareCall(sql)
        ) {
            consulta.setInt(1, id);
            consulta.setString(2, rol.toLowerCase());

            int filasAfectadas = consulta.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar el rol del usuario: " + e.getMessage());
            return false;
        }
    }
}