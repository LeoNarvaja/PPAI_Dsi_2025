package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "Cambio_estado")
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    @OneToMany(mappedBy = "cambioEstado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotivoFueraServicio> motivosFueraServicio = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_sismografo", referencedColumnName = "identificadorSismografo")
    private Sismografo sismografo;

    public CambioEstado() {
    }

    public CambioEstado(LocalDateTime fechaHoraInicio, Empleado empleado, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.empleado = empleado;
        this.estado = estado;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public List<MotivoFueraServicio> getMotivoFueraServicio() {
        return motivosFueraServicio;
    }

    public void setMotivoFueraServicio(List<MotivoFueraServicio> motivoFueraServicio) {
        this.motivosFueraServicio = motivoFueraServicio;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Sismografo getSismografo() {
        return sismografo;
    }

    public void setSismografo(Sismografo sismografo) {
        this.sismografo = sismografo;
    }

    public boolean esActual() {
        if (this.fechaHoraFin == null) {
            return true;
        }
        return false;
    }

    public void crearMotivoFueraServicio(Map<MotivoTipo, String> comentarioMotivoFueraServicio) {
        for (Map.Entry<MotivoTipo, String> entry : comentarioMotivoFueraServicio.entrySet()) {
            MotivoTipo motivoTipo = entry.getKey();
            String comentario = entry.getValue();
            MotivoFueraServicio motivoFueraServicio = new MotivoFueraServicio(motivoTipo, comentario);
            motivoFueraServicio.setCambioEstado(this);
            this.motivosFueraServicio.add(motivoFueraServicio);
        }
    }

    @Override
    public String toString() {
        return "CambioEstado{" +
                " fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", motivoFueraServicio=" + motivosFueraServicio +
                ", empleado=" + empleado +
                ", estado=" + estado +
                '}';
    }

}
