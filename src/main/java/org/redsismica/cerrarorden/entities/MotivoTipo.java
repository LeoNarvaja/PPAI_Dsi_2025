package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Motivo_tipo")
public class MotivoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String descripcion;

    @OneToMany(mappedBy = "motivoTipo")
    private List<MotivoFueraServicio> motivosFueraServicio;

    public MotivoTipo() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "MotivoTipo{" +
                "descripcion='" + descripcion + '\'' +
                '}';
    }

}
