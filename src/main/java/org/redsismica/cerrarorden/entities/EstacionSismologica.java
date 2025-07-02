package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Estacion_sismologica")
public class EstacionSismologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigoEstacion;

    private String nombre;

    @OneToMany(mappedBy = "estacionSismologica")
    private List<OrdenInspeccion> ordenesInspeccion;

    @OneToOne(mappedBy = "estacionSismologica")
    private Sismografo sismografo;

    public EstacionSismologica() {}

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstacionSismologica that)) return false;
        return codigoEstacion == that.codigoEstacion;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigoEstacion);
    }

}
