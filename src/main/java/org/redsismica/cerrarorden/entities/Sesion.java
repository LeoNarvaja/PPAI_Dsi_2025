package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Sesion")
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Atributo para el caso de uso
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    public Sesion() {
    }

    public Empleado obtenerUsuario() {
        return usuario.obtenerEmpleado();
    }

}
