package org.redsismica.cerrarorden.dto;
import org.redsismica.cerrarorden.entities.MotivoTipo;

public class MotivoTipoDTO {

    private final MotivoTipo motivoTipo;
    private final String descripcion;

    public MotivoTipoDTO(MotivoTipo motivoTipo, String descripcion) {
        this.motivoTipo = motivoTipo;
        this.descripcion = descripcion;
    }

    // Getters
    public MotivoTipo getMotivoTipo() {
        return motivoTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}