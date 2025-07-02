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

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    @OneToMany(mappedBy = "cambioEstado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotivoFueraServicio> motivosFueraServicio = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

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

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }


    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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
            // llama al constructor de la clase motivo fuera de servicio con los parametros correspondientes
            // Se crea un nuevo motivo
            MotivoFueraServicio motivoFueraServicio = new MotivoFueraServicio(motivoTipo, comentario);
            motivoFueraServicio.setCambioEstado(this);
            this.motivosFueraServicio.add(motivoFueraServicio);
        }
    }
}
