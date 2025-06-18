package org.redsismica.cerrarorden.control;

import org.redsismica.cerrarorden.boundary.InterfazMail;
import org.redsismica.cerrarorden.boundary.MonitorCCRS;
import org.redsismica.cerrarorden.boundary.PantallaCierreOrdenInspeccion;
import org.redsismica.cerrarorden.dtos.OrdenInspeccionDTO;
import org.redsismica.cerrarorden.entities.*;
import org.redsismica.cerrarorden.services.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class GestorCierreOrdenInspeccion {

    private OrdenInspeccionService ordenInspeccionService;

    private SesionService sesionService;

    private SismografoService sismografoService;

    private MotivoTipoService motivoTipoService;

    private EstadoService estadoService;

    private PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion;
    private Sesion sesionActual;
    private List<MotivoTipo> motivosTipos = new ArrayList<>();
    private ArrayList<Estado> estados = new ArrayList<>();

    private List<Empleado> empleados = new ArrayList<>();
    private OrdenInspeccion ordenInspeccionSeleccionada;
    private Sismografo sismografoDeLaEstacion;

    private ArrayList<MotivoTipo> motivosTiposSeleccionados = new ArrayList<>();
    private Map<MotivoTipo, String> comentariosMotivosFueraServicio = new HashMap<>();

    private Estado estadoCerrado;
    private LocalDateTime fechaHoraActual;
    private Estado estadoFueraServicio;
    private Empleado empleadoLogueado;
    private String observacionCierre;

    private InterfazMail interfazMail;
    private List<MonitorCCRS> monitores;

    public GestorCierreOrdenInspeccion(PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion) {
        this.pantallaCierreOrdenInspeccion = pantallaCierreOrdenInspeccion;
        this.ordenInspeccionService = new OrdenInspeccionService();
        this.sesionService = new SesionService();
        this.sismografoService = new SismografoService();
        this.motivoTipoService = new MotivoTipoService();
        this.estadoService = new EstadoService();
    }

    public void nuevoCierreOrdenInspeccion() throws IOException {
        this.sesionActual = sesionService.obtenerSesionActiva("juan.perez@example.com");
        this.empleadoLogueado = buscarEmpleado();
        pantallaCierreOrdenInspeccion.cargarDatosIniciales(empleadoLogueado.getEmail());
        buscarOrdenesInspeccion(this.empleadoLogueado);
    }

    public Empleado buscarEmpleado() {
        return sesionActual.obtenerUsuario();
    }

    public void buscarOrdenesInspeccion(Empleado empleadoLogueado) {
        List<OrdenInspeccionDTO> ordenesDeEmpleado = new ArrayList<>();
        List<OrdenInspeccion> ordenesDeInspeccion = this.ordenInspeccionService.listarOrdenesInspeccion();
        List<Sismografo> sismografos = this.sismografoService.obtenerSismografos();
        for (OrdenInspeccion orden : ordenesDeInspeccion) {
            if (orden.esDeEmpleado(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                for (Sismografo sismografo : sismografos) {
                    if (sismografo.esTuEstacion(orden.getEstacionSismologica())) {
                        ordenesDeEmpleado.add(new OrdenInspeccionDTO(orden, sismografo));
                        break;
                    }
                }
            }
        }
        ordenarOrdenesInpeccion(ordenesDeEmpleado);
        this.pantallaCierreOrdenInspeccion.mostrarDatosOrdenesinspeccion(ordenesDeEmpleado);
        this.pantallaCierreOrdenInspeccion.solicitarSeleccionOrdenInspeccion();
    }

    private void ordenarOrdenesInpeccion(List<OrdenInspeccionDTO> ordenesInspeccion) {
        ordenesInspeccion.sort(Comparator.comparing(
                ordenInspeccionDTO -> ordenInspeccionDTO.getOrdenInspeccion().getFechaHoraFinalizacion())
        );
    }

    public void tomarSeleccionOrdenInspeccion(OrdenInspeccionDTO seleccion) {
        this.ordenInspeccionSeleccionada = seleccion.getOrdenInspeccion();
        this.sismografoDeLaEstacion = seleccion.getSismografo();
        this.pantallaCierreOrdenInspeccion.solicitarObservacion();
    }

    public void tomarObservacion(String observacion) {
        this.observacionCierre = observacion;
        this.buscarTiposMotivos();
    }

    public void buscarTiposMotivos() {
        List<MotivoTipo> motivosTipos = this.motivoTipoService.obtenerMotivosTipo();
        pantallaCierreOrdenInspeccion.solicitarSeleccionMotivoTipo(motivosTipos);
    }

    public void tomarSeleccionMotivosTipo(Map<MotivoTipo, String> comentariosMotivosFueraServicio) {
        this.comentariosMotivosFueraServicio = comentariosMotivosFueraServicio;
        this.motivosTiposSeleccionados = new ArrayList<>(comentariosMotivosFueraServicio.keySet());
    }

    public void tomarConfirmacionCierreOrden() {
        if (ordenInspeccionSeleccionada == null) {
            throw new RuntimeException("Debe seleccionar una orden de inspeccion");
        }
        if (!validarObservacionCierre()) {
            throw new RuntimeException("Debe ingresar una observacion de cierre");
        }
        if (comentariosMotivosFueraServicio.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un motivo para fuera de servicio");
        }

        // Verificar que todos los motivos tengan comentarios
        boolean hayComentariosVacios = comentariosMotivosFueraServicio.values().stream()
                .anyMatch(comentario -> comentario == null || comentario.trim().isEmpty());

        if (hayComentariosVacios) {
            throw new RuntimeException("Debe completar el comentario para todos los motivos seleccionados");
        }
        this.getFechaYHora();
        this.buscarEstadoCierre();
    }

    public void getFechaYHora() {
        this.fechaHoraActual = LocalDateTime.now();
    }

    public void buscarEstadoCierre() {
        List<Estado> estados = this.estadoService.obtenerEstados();
        for (Estado estado : estados) {
            if (estado.esAmbitoOrdenInspeccion() && estado.esCerrada()) {
                this.estadoCerrado = estado;
            }
        }
        this.registrarCierreOrdenInspeccion(this.fechaHoraActual, this.estadoCerrado, this.observacionCierre);
    }

    public void registrarCierreOrdenInspeccion(LocalDateTime fechaHoraCierre, Estado estado, String observacionCierre) {
        this.ordenInspeccionSeleccionada.cerrar(fechaHoraCierre, estado, observacionCierre);
        buscarEstadoFueraServicio();
        // this.notificarEmpleados();
        // this.publicarNotificaciones();
    }

    public void buscarEstadoFueraServicio() {
        List<Estado> estados = this.estadoService.obtenerEstados();
        for (Estado estado : estados) {
            if (estado.esAmbitoSismografo() && estado.esFueraServicio()) {
                this.estadoFueraServicio = estado;
            }
        }
        sismografoDeLaEstacion.actualizarAFueraServicio(
                fechaHoraActual,
                empleadoLogueado,
                estadoFueraServicio,
                comentariosMotivosFueraServicio);
        this.finCU();
    }

    public boolean validarObservacionCierre() {
        if (this.observacionCierre.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validarMotivosTipo() {
        if (motivosTiposSeleccionados.isEmpty()) {
            return false;
        }
        return true;
    }

    public void notificarEmpleados() {
        String email = "";
        for (Empleado empleado : empleados) {
            if (empleado.esResponsableDeReparacion()) {
                email = empleado.getEmail();
                this.interfazMail.enviarNotificacionMail();
            }
        }
    }

    public void publicarNotificacion() {
        for (MonitorCCRS monitorCCRS : monitores) {
            monitorCCRS.publicar();
        }
    }

    public void finCU() {
        this.sismografoService.actualizarSismografo(sismografoDeLaEstacion);
        this.ordenInspeccionService.actualizarOrdenInspeccion(ordenInspeccionSeleccionada);
        this.reiniciarDatos();
    }

    public void reiniciarDatos() {
        // Limpiar la observación de la orden antes de resetear la referencia
        if (this.ordenInspeccionSeleccionada != null) {
            this.ordenInspeccionSeleccionada.setObservacionCierre("");
        }

        // Limpiar TODOS los atributos del proceso
        this.ordenInspeccionSeleccionada = null;
        this.observacionCierre = "";
        this.sismografoDeLaEstacion = null;
        this.motivosTiposSeleccionados.clear();
        this.comentariosMotivosFueraServicio.clear();
        this.estadoCerrado = null;
        this.estadoFueraServicio = null;
        this.fechaHoraActual = null;
        this.empleadoLogueado = null;
    }
}
