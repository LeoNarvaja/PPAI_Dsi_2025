package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "rol")
    private List<Empleado> empleados;

    private String nombreRol;

    public String getNombreRol() {
        return nombreRol;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "nombreRol='" + nombreRol + '\'' +
                '}';
    }


}
