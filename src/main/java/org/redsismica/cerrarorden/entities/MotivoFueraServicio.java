package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Motivo_Fuera_Servicio")
public class MotivoFueraServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMotivo;

    @ManyToOne
    @JoinColumn(name = "id_motivo_tipo", referencedColumnName = "id")
    private MotivoTipo motivoTipo;

    @ManyToOne
    @JoinColumn(name = "id_cambio_estado", referencedColumnName = "id")
    private CambioEstado cambioEstado;

    private String comentario;

    public MotivoFueraServicio() {
    }

    public MotivoFueraServicio(MotivoTipo motivoTipo, String comentario) {
        this.motivoTipo = motivoTipo;
        this.comentario = comentario;
    }

    public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public MotivoTipo getMotivoTipo() {
        return motivoTipo;
    }

    public void setMotivoTipo(MotivoTipo motivoTipo) {
        this.motivoTipo = motivoTipo;
    }

    public CambioEstado getCambioEstado() {
        return cambioEstado;
    }

    public void setCambioEstado(CambioEstado cambioEstado) {
        this.cambioEstado = cambioEstado;
    }

    @Override
    public String toString() {
        return "MotivoFueraServicio{" +
                "comentario='" + comentario + '\'' +
                ", motivoTipo=" + motivoTipo +
                '}';
    }

}
