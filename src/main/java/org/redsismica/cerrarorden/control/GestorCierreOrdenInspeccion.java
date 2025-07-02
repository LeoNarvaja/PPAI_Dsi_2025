package org.redsismica.cerrarorden.control;

import org.redsismica.cerrarorden.boundary.InterfazMail;
import org.redsismica.cerrarorden.boundary.MonitorCCRS;
import org.redsismica.cerrarorden.boundary.PantallaCierreOrdenInspeccion;
import org.redsismica.cerrarorden.dto.MotivoTipoDTO;
import org.redsismica.cerrarorden.dto.OrdenInspeccionDTO;
import org.redsismica.cerrarorden.entities.*;
import org.redsismica.cerrarorden.services.*;

import java.time.LocalDateTime;
import java.util.*;

public class GestorCierreOrdenInspeccion {

    // ================== Servicios ========================
    private OrdenInspeccionService ordenInspeccionService;
    private SesionService sesionService;
    private SismografoService sismografoService;
    private MotivoTipoService motivoTipoService;
    private EstadoService estadoService;
    private EmpleadoService empleadoService;

    // ================== Caso de uso =================================
    private Sesion sesionActual;
    private Map<MotivoTipo, String> comentariosMotivosFueraServicio = new HashMap<>();
    private List<OrdenInspeccionDTO> datosOrdenesInspeccion = new ArrayList<>();
    private List<MotivoTipoDTO> descripcionMotivosTipo = new ArrayList<>();
    private Empleado empleado;
    private Estado estadoCerrada;
    private Estado estadoFueraServicio;
    private LocalDateTime fechaYHoraActual;
    private String emailEmpleado;
    private Set<MotivoTipo> motivosTipoSeleccionados = new HashSet<>();
    private String observacion;
    private OrdenInspeccion ordenInspeccionSeleccionada;
    private Sismografo sismografoDeLaEstacion;

    private PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion;
    private InterfazMail interfazMail;
    private List<MonitorCCRS> monitores;
    private Boolean datosInicializados = false;

    // ================ Colecciones de datos ============================
    List<OrdenInspeccion> ordenesDeInspeccion = new ArrayList<>();
    List<Sismografo> sismografos = new ArrayList<>();
    List<MotivoTipo> motivosTipos = new ArrayList<>();
    List<Estado> estados = new ArrayList<>();
    List<Empleado> empleados = new ArrayList<>();

    // Constructor del gestor con la referencia a la pantalla
    public GestorCierreOrdenInspeccion(PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion) {
        this.pantallaCierreOrdenInspeccion = pantallaCierreOrdenInspeccion;
        // Servicios de las entidades para poder obtener las colecciones desde la base de datos
        this.ordenInspeccionService = new OrdenInspeccionService();
        this.sesionService = new SesionService();
        this.sismografoService = new SismografoService();
        this.motivoTipoService = new MotivoTipoService();
        this.estadoService = new EstadoService();
        this.empleadoService = new EmpleadoService();
    }

    // Se da inicio al cierre de la orden de inspeccion
    public void nuevoCierreOrdenInspeccion() {
        // Limpiar atributos previos
        this.reiniciarDatos();
        // Obtener las colecciones de objetos necesarios desde la base de datos
        if (!datosInicializados) {
            this.ordenesDeInspeccion = this.ordenInspeccionService.listarOrdenesInspeccion();
            this.sismografos = this.sismografoService.obtenerSismografos();
            this.motivosTipos = this.motivoTipoService.obtenerMotivosTipo();
            this.estados = this.estadoService.obtenerEstados();
            this.empleados = this.empleadoService.obtenerEmpleados();
            // Simula ser un inicio de sesion
            this.sesionActual = sesionService.obtenerSesionActiva("juan.perez@example.com");
            this.datosInicializados = true;
        }
        // Busca al empleado logueado a traves de la sesion activa
        this.empleado = buscarEmpleado();
        buscarOrdenesInspeccion(this.empleado);
    }

    // Se busca al empleado a traves de la sesion retornando una referencia a ese objeto empleado
    public Empleado buscarEmpleado() {
        return sesionActual.obtenerUsuario();
    }

    public void buscarOrdenesInspeccion(Empleado empleadoLogueado) {
        for (OrdenInspeccion orden : ordenesDeInspeccion) {
            // Con la referencia al empleado obtengo las ordenes y aquellas que estan en estado completamente realizada
            if (orden.esDeEmpleado(empleadoLogueado) && orden.esCompletamenteRealizada()) {
                int numeroOrden = orden.getNumeroOrden();
                String fechaFin = orden.getFechaHoraFinalizacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String estacionSismologica = orden.buscarEstacionSismologica();
                for (Sismografo sismografo : sismografos) {
                    if (sismografo.esTuEstacion(orden.getEstacionSismologica())) {
                        sismografoDeLaEstacion = sismografo;
                        int identificadorSismografo = sismografoDeLaEstacion.getIdentificadorSismografo();
                        // Se agregan los datos de las ordenes filtadas, guardando ademas una referencia a la orden actual y al sismografo correspondiente
                        datosOrdenesInspeccion.add(
                                new OrdenInspeccionDTO(numeroOrden, fechaFin, estacionSismologica, identificadorSismografo, orden, sismografo)
                        );
                        break; // Detengo la ejecucion al encontrar el sismografo
                    }
                }
            }
        }
        // Se ordenan los datos de las ordenes y se llama a la pantalla para que muestre dichas ordenes
        ordenarOrdenesInspeccion(datosOrdenesInspeccion);
        this.pantallaCierreOrdenInspeccion.mostrarDatosOrdenesinspeccion(datosOrdenesInspeccion);
        this.pantallaCierreOrdenInspeccion.solicitarSeleccionOrdenInspeccion();
    }

    private void ordenarOrdenesInspeccion(List<OrdenInspeccionDTO> ordenesInspeccion) {
        ordenesInspeccion.sort(Comparator.comparing(
                ordenInspeccionDTO -> ordenInspeccionDTO.getOrdenInspeccion().getFechaHoraFinalizacion())
        );
    }

    // Recibe la seleccion con los datos de la orden
    public void tomarSeleccionOrdenInspeccion(OrdenInspeccionDTO ordenInspeccionDTO) {
        this.ordenInspeccionSeleccionada = ordenInspeccionDTO.getOrdenInspeccion(); // Obtengo la referencia a la orden seleccionada
        this.sismografoDeLaEstacion = ordenInspeccionDTO.getSismografo(); // Obtengo la referencia del sismografo correspondiendo a esa orden
        // Llama a la pantalla para permitir ingresar la observacion de cierre
        this.pantallaCierreOrdenInspeccion.solicitarObservacion();
    }

    // Recibe la observacion ingresada por el usuario en la pantalla
    public void tomarObservacion(String observacion) {
        this.observacion = observacion;
        // llama al metodo para buscar los tipos de motivos por los que un sismografo se puede poner
        // fuera de servicio
        this.buscarTiposMotivos();
    }

    public void buscarTiposMotivos() {
        // De cada motivo debo obtener su descripcion
        for(MotivoTipo motivo : motivosTipos) {
            String descripcion = motivo.getDescripcion();
            // Una vez obtenida la descripcion, envio los datos junto con la referencia al motivo correspondiente
            MotivoTipoDTO motivoData = new MotivoTipoDTO(motivo, descripcion);
            this.descripcionMotivosTipo.add(motivoData);
        }
        // Envia a la pantalla los datos de los motivos correspondientes
        pantallaCierreOrdenInspeccion.solicitarSeleccionMotivoTipo(this.descripcionMotivosTipo);
    }

    public void tomarSeleccionMotivoTipo(MotivoTipoDTO motivoTipoDTO, Boolean seleccionado) {
        // Obtengo la referencia del motivo tipo
        MotivoTipo motivoTipo = motivoTipoDTO.getMotivoTipo();
        // Valido que exista un motivo seleccionado y no haya sido borrada la seleccion posteriormente
        if (motivoTipo != null && seleccionado) {
            // Agrego los motivos a la lista de motivos seleccionados
                motivosTipoSeleccionados.add(motivoTipo);
                // Actualizo la estructura para mantener asociado el motivo con su comentario
                // incialmente, se asigna un comentario vacio
                comentariosMotivosFueraServicio.put(motivoTipo, "");
            } else {
                // En caso que no haya motivo o se eliminea la seleccion se debe actualizar otra vez los motivos
                motivosTipoSeleccionados.remove(motivoTipo);
                comentariosMotivosFueraServicio.remove(motivoTipo);
            }
    }

    // Recibe el comentario ingresado de cada motivo
    public void tomarComentario(MotivoTipoDTO motivoTipoDTO, String comentario) {
        // Obtengo la referencia al motivo
        MotivoTipo motivoTipo = motivoTipoDTO.getMotivoTipo();
        // Actualiza el comentario para los motivos seleccionados
        if (motivosTipoSeleccionados.contains(motivoTipo)) {
            comentariosMotivosFueraServicio.put(motivoTipo, comentario);
        }
    }

    // Realiza las validaciones correspondientes,
    public void tomarConfirmacionCierreOrden() {
        if (ordenInspeccionSeleccionada == null) {
            throw new RuntimeException("Debe seleccionar una orden de inspeccion");
        }
        if (!validarObservacionCierre()) {
            throw new RuntimeException("Debe ingresar una observacion de cierre");
        }
        if (!validarMotivosTipo()) {
            throw new RuntimeException("Debe seleccionar al menos un motivo para fuera de servicio");
        }

        // Verificar que todos los motivos tengan comentarios
        if (comentariosMotivosFueraServicio.values().stream().anyMatch(
                        comentario -> comentario == null || comentario.trim().isEmpty())) {
            throw new RuntimeException("Debe completar el comentario para todos los motivos seleccionados");
        }
        // Si las validaciones son correctas se obtiene la fecha y hora actual y procede a cerrarse la orden
        this.getFechaYHora();
        this.buscarEstadoCierre();
    }

    public void getFechaYHora() {
        this.fechaYHoraActual = LocalDateTime.now();
    }

    // Busca de todos los estados, aquellos que son de orden de inspeccion y obtengo la referencia al estado cerrada
    public void buscarEstadoCierre() {
        for (Estado estado : estados) {
            if (estado.esAmbitoOrdenInspeccion() && estado.esCerrada()) {
                this.estadoCerrada = estado;
            }
        }
        // Llama al metodo para registrar el cierre de la orden, pasando como parametro la fecha y hora actual
        // obtenida, la referencia al estado y la observacion de cierre ingresada por el usuario
        this.registrarCierreOrdenInspeccion(this.fechaYHoraActual, this.estadoCerrada, this.observacion);
    }

    public void registrarCierreOrdenInspeccion(LocalDateTime fechaHoraCierre, Estado estado, String observacionCierre) {
        this.ordenInspeccionSeleccionada.cerrar(fechaHoraCierre, estado, observacionCierre);
        buscarEstadoFueraServicio();
        // this.notificarEmpleados();
        // this.publicarNotificaciones();
    }

    // Busca la referencia al estado fuera de servicio para actualizar el estado del sismografo
    // y establecer un nuevo cambio de estado
    public void buscarEstadoFueraServicio() {
        for (Estado estado : estados) {
            if (estado.esAmbitoSismografo() && estado.esFueraServicio()) {
                this.estadoFueraServicio = estado;
            }
        }
        // Actualiza el sismografo como fuera de servicio enviando como parametros la fecha hora actual, el empleado
        // responsable del cambio de estado, la referencia la estado y los motivos con sus comentarios asociados.
        sismografoDeLaEstacion.actualizarAFueraServicio(
                fechaYHoraActual,
                empleado,
                estadoFueraServicio,
                comentariosMotivosFueraServicio);
        this.finCU();
    }

    public boolean validarObservacionCierre() {
        if (this.observacion.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validarMotivosTipo() {
        if (motivosTipoSeleccionados.isEmpty()) {
            return false;
        }
        return true;
    }

    public void notificarEmpleados() {
        for (Empleado empleado : empleados) {
            if (empleado.esResponsableDeReparacion()) {
                this.emailEmpleado = empleado.getEmail();
                this.interfazMail.enviarNotificacionMail(emailEmpleado, datosOrdenesInspeccion);
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
    }

    public void reiniciarDatos() {
        // Limpiar la observación de la orden antes de resetear la referencia
        if (this.ordenInspeccionSeleccionada != null) {
            this.ordenInspeccionSeleccionada.setObservacionCierre("");
        }

        // Limpiar TODOS los atributos del proceso
        this.sesionActual = null;
        this.comentariosMotivosFueraServicio.clear();
        this.datosOrdenesInspeccion.clear();
        this.descripcionMotivosTipo.clear();
        this.empleado = null;
        this.estadoCerrada = null;
        this.estadoFueraServicio = null;
        this.fechaYHoraActual = null;
        this.emailEmpleado = null;
        this.motivosTipoSeleccionados.clear();
        this.observacion = "";
        this.ordenInspeccionSeleccionada = null;
        this.sismografoDeLaEstacion = null;
        datosInicializados = false;
    }
}
