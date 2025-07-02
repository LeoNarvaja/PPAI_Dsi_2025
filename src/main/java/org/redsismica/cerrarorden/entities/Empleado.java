package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    @ManyToOne
    @JoinColumn(name = "id_rol", referencedColumnName = "id")
    private Rol rol;

    @OneToMany(mappedBy = "empleado")
    private List<OrdenInspeccion> ordenesInspeccion;

    @OneToMany(mappedBy = "empleado")
    private List<CambioEstado> cambiosEstado;

    @OneToOne(mappedBy = "empleado")
    private Usuario usuario;

    public Empleado() {
    }

    public boolean esResponsableDeReparacion() {
        return Objects.equals(this.rol.getNombreRol(), "Responsable de reparacion");
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empleado empleado)) return false;
        return Objects.equals(email, empleado.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

}
