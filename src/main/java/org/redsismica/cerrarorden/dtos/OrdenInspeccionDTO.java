package org.redsismica.cerrarorden.dtos;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.entities.Sismografo;

public class OrdenInspeccionDTO {

    private final OrdenInspeccion ordenInspeccion;
    private final Sismografo sismografo;

    public OrdenInspeccionDTO(OrdenInspeccion ordenInspeccion, Sismografo sismografo) {
        this.ordenInspeccion = ordenInspeccion;
        this.sismografo = sismografo;
    }

    public OrdenInspeccion getOrdenInspeccion() {
        return ordenInspeccion;
    }

    public Sismografo getSismografo() {
        return sismografo;
    }

    // --- Métodos Property para que TableView los use directamente ---

    public StringProperty numeroOrdenProperty() {
        return new SimpleStringProperty(String.valueOf(ordenInspeccion.getNumeroOrden()));
    }

    public StringProperty fechaFinalizacionProperty() {
        return new SimpleStringProperty(ordenInspeccion.getFechaHoraFinalizacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public StringProperty estacionProperty() {
        // Asumiendo que buscarEstacionSismologica() devuelve el nombre de la estación
        return new SimpleStringProperty(ordenInspeccion.buscarEstacionSismologica());
    }

    public StringProperty identificadorSismografoProperty() {
        return new SimpleStringProperty(String.valueOf(sismografo.getIdentificadorSismografo()));
    }

}
