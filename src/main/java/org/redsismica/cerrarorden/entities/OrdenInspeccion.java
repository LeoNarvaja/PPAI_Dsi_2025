package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;


// ============= Clase para modelo de datos =============
@Entity
@Table(name = "Orden_inspeccion")
public class OrdenInspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numeroOrden;

    private LocalDateTime fechaHoraCierre;

    private LocalDateTime fechaHoraFinalizacion;

    private LocalDateTime fechaHoraInicio;

    @ManyToOne
    @JoinColumn(name = "cod_estacion", referencedColumnName = "codigoEstacion")
    private EstacionSismologica estacionSismologica;

    @ManyToOne
    @JoinColumn(name = "id_estado", referencedColumnName = "id")
    private Estado estado;

    private String observacionCierre;

    @ManyToOne
    @JoinColumn(name = "id_empleado", referencedColumnName = "id")
    private Empleado empleado;

    public OrdenInspeccion() {
    }

    public void setFechaHoraCierre(LocalDateTime fechHoraCierre) {
        this.fechaHoraCierre = fechHoraCierre;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public LocalDateTime getFechaHoraFinalizacion() {
        return fechaHoraFinalizacion;
    }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public boolean esDeEmpleado(Empleado empleado) {
        if (empleado == null || this.empleado == null) {
            return false;
        }
        return this.empleado.equals(empleado);
    }

    public boolean esCompletamenteRealizada() {
        return this.estado.esCompletamenteRealizada();
    }

    public String buscarEstacionSismologica() {
        return this.estacionSismologica.getNombre();
    }

    public int buscarCodigoEstacion() {
        return this.estacionSismologica.getCodigoEstacion();
    }

    public void cerrar(LocalDateTime fechaHoraCierre, Estado estado, String observacionCierre) {
        this.setFechaHoraCierre(fechaHoraCierre);
        this.setEstado(estado);
        this.setObservacionCierre(observacionCierre);
    }

    @Override
    public String toString() {
        return "OrdenInspeccion{" +
                "numeroOrden=" + numeroOrden +
                ", fechHoraCierre=" + fechaHoraCierre +
                ", fechHoraFinalizacion=" + fechaHoraFinalizacion +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", estacionSismologica=" + estacionSismologica +
                ", estado=" + estado +
                ", observacionCierre='" + observacionCierre + '\'' +
                ", empleado=" + empleado +
                '}';
    }

}
