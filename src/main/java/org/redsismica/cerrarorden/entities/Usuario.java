package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "id_empleado", referencedColumnName = "id")
    private Empleado empleado;

    @OneToOne(mappedBy = "usuario")
    private Sesion sesion;

    public Usuario() {
    }

    public Usuario(Empleado empleado) {
        this.empleado = empleado;
    }

    public Empleado obtenerEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

}
