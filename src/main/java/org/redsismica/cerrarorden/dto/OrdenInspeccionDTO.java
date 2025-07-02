package org.redsismica.cerrarorden.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.entities.Sismografo;

import java.time.LocalDateTime;

public class OrdenInspeccionDTO {

    private int numeroOrden;
    private String fechaFin;
    private String nombreEstacion;
    private int identificadorSismografo;
    private OrdenInspeccion ordenInspeccion;
    private Sismografo sismografo;

    public OrdenInspeccionDTO(int numeroOrden, String fechaFin, String nombreEstacion, int identificadorSismografo, OrdenInspeccion ordenInspeccion, Sismografo sismografo) {
        this.numeroOrden = numeroOrden;
        this.fechaFin = fechaFin;
        this.nombreEstacion = nombreEstacion;
        this.identificadorSismografo = identificadorSismografo;
        this.ordenInspeccion = ordenInspeccion;
        this.sismografo = sismografo;
    }

    public OrdenInspeccion getOrdenInspeccion() {
        return ordenInspeccion;
    }

    public Sismografo getSismografo() {
        return sismografo;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getNombreEstacion() {
        return nombreEstacion;
    }

    public int getIdentificadorSismografo() {
        return identificadorSismografo;
    }

    public void setNumeroOrden(int numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setNombreEstacion(String nombreEstacion) {
        this.nombreEstacion = nombreEstacion;
    }

    public void setIdentificadorSismografo(int identificadorSismografo) {
        this.identificadorSismografo = identificadorSismografo;
    }

    public void setOrdenInspeccion(OrdenInspeccion ordenInspeccion) {
        this.ordenInspeccion = ordenInspeccion;
    }

    public void setSismografo(Sismografo sismografo) {
        this.sismografo = sismografo;
    }
}
