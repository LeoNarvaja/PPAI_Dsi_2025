package org.redsismica.cerrarorden.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "Sismografo")
public class Sismografo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identificadorSismografo;

    @ManyToOne
    @JoinColumn(name = "id_estado", referencedColumnName = "id")
    private Estado estadoActual;

    @OneToOne
    @JoinColumn(name = "codigo_estacion", referencedColumnName = "codigoEstacion")
    private EstacionSismologica estacionSismologica;

    @OneToMany(mappedBy = "sismografo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CambioEstado> cambiosEstado;

    public Sismografo() {}

    public int getIdentificadorSismografo() {
        return identificadorSismografo;
    }

    public void setEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
    }

    public boolean esTuEstacion(EstacionSismologica estacionSismologica) {
        if (this.estacionSismologica == null || estacionSismologica == null) {
            return false;
        }
        return this.estacionSismologica.equals(estacionSismologica);
    }

    public void actualizarAFueraServicio(LocalDateTime fechaHoraActual, Empleado empleado, Estado estado, Map<MotivoTipo, String> comentarioMotivoFueraServicio) {
        this.setEstadoActual(estado);
        // Debo obtener el cambio de estado actual desde el sismografo, que conoce sus estados
        CambioEstado cambioEstadoActual = this.buscarEstadoActual();
        // Establezco la fecha hora de fin del estado actual
        cambioEstadoActual.setFechaHoraFin(fechaHoraActual);
        // Crea un nuevo cambio de estado
        this.crearCambioEstado(
                fechaHoraActual,
                empleado,
                estado,
                comentarioMotivoFueraServicio);
    }

    public CambioEstado buscarEstadoActual() {
        for (CambioEstado cambioEstado : this.cambiosEstado) {
            if (cambioEstado.esActual()) {
                return cambioEstado;
            }
        }
        return null;
    }

    public void crearCambioEstado(LocalDateTime fechaHoraActual,Empleado empleado, Estado estado, Map<MotivoTipo, String> comentarioMotivoFueraServicio) {
        // Llama al constructor de la clase cambio de estado con los parametros correspondientes
        CambioEstado nuevoCambioEstado = new CambioEstado(fechaHoraActual, empleado, estado);
        nuevoCambioEstado.setSismografo(this);
        this.cambiosEstado.add(nuevoCambioEstado);
        // El cambio de estado es reponsable de crear los motivos por los que se pone fuera de servicio el sismografo con sus comentarios
        nuevoCambioEstado.crearMotivoFueraServicio(comentarioMotivoFueraServicio);
    }

}

