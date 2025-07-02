package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Estado")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ambito;

    private String nombreEstado;

    @OneToMany(mappedBy = "estado")
    private List<OrdenInspeccion> ordenInspeccion;

    @OneToMany(mappedBy = "estado")
    private List<CambioEstado> cambiosEstado;

    @OneToMany(mappedBy = "estadoActual")
    private List<Sismografo> sismografos;

    public Estado() {
    }

    public boolean esCompletamenteRealizada() {
        return nombreEstado.equals("Completamente realizada");
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public boolean esAmbitoOrdenInspeccion() {
        return this.ambito.equals("Orden de inspeccion");
    }

    public boolean esCerrada() {
        return this.nombreEstado.equals("Cerrada");
    }

    public boolean esAmbitoSismografo() {
        return ambito.equals("Sismografo");
    }

    public boolean esFueraServicio() {
        return nombreEstado.equals("Fuera de servicio");
    }

}
