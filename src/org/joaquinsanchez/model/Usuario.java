package org.joaquinsanchez.model;

import java.sql.Timestamp;

public class Usuario {

    private int id;
    private String username;
    private String rol;
    private boolean activo;
    private Timestamp fechaCreacion;

    public Usuario() {
    }

    public Usuario(
            int id,
            String username,
            String rol,
            boolean activo,
            Timestamp fechaCreacion
    ) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}