package com.clinica.arches.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "preferencias_usuario", schema = "clinica")
public class PreferenciaUsuario {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "notificaciones_correo", nullable = false)
    private boolean notificacionesCorreo = true;

    @Column(name = "foto", columnDefinition = "text")
    private String foto;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public PreferenciaUsuario() {}

    public PreferenciaUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @PreUpdate
    @PrePersist
    void tocar() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public boolean isNotificacionesCorreo() { return notificacionesCorreo; }
    public void setNotificacionesCorreo(boolean v) { this.notificacionesCorreo = v; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
