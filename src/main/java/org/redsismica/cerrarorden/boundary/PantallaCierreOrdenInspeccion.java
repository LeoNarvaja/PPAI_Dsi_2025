package org.redsismica.cerrarorden.boundary;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.redsismica.cerrarorden.control.GestorCierreOrdenInspeccion;
import org.redsismica.cerrarorden.dto.MotivoTipoDTO;
import org.redsismica.cerrarorden.dto.OrdenInspeccionDTO;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PantallaCierreOrdenInspeccion implements Initializable {

    private GestorCierreOrdenInspeccion gestorCierreOrdenInspeccion;

    private static final Logger LOGGER = Logger.getLogger(PantallaCierreOrdenInspeccion.class.getName());

    // ================== Atributos para el CU =========================
    @FXML private Button buttonConfirmar;
    @FXML private TableView<OrdenInspeccionDTO> tablaOrdenes;
    @FXML private TextArea txtObservacion;
    @FXML private CheckBox checkboxTipoMotivo;
    @FXML private HBox lblOrdenInspeccion;
    @FXML private TextArea txtComentario;


    // ================= Secciones y configuracion de componentes =========
    @FXML private VBox panelBienvenida;
    @FXML private ScrollPane scrollInspeccion;

    @FXML private TableColumn<OrdenInspeccionDTO, String> colNumOrden;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colFechaFinalizacion;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colEstacion;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colSismografo;

    // ================== Atributos adicionales =========================
    @FXML private VBox seccionObservacion;
    @FXML private VBox seccionMotivos;
    @FXML private Label lblIdSismografo;
    @FXML private RadioButton rbOnline;
    @FXML private RadioButton rbFueraServicio;
    @FXML private VBox vboxMotivosFuera;
    private ToggleGroup estadoToggleGroup;

    // ================== Constructor de la clase pantalla =========================
    // Inicializa al gestor pasandose la misma pantalla como parametro en el constructor del gestor para manejo de dependencia
    public PantallaCierreOrdenInspeccion() {
        this.gestorCierreOrdenInspeccion = new GestorCierreOrdenInspeccion(this);
    }

    // Cuando el usuario presiona el boton para dar cierre a la orden de inspeccion se habilita la ventana principal
    @FXML
    private void opcDarCierreOrdenInspeccion() {
        try {
            this.habilitarVentana(); //
            LOGGER.info("Pantalla habilitada");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error mostrando la panatalla", e);
        }
    }

    // Carga todos los componentes de la pantalla necesarios
    private void habilitarVentana() {
        // Habilita el panel principal de ordenes
        panelBienvenida.setVisible(false);
        scrollInspeccion.setVisible(true);
        scrollInspeccion.setVvalue(0);
        // Habilita seccion para cambio de estado de sismografo
        rbOnline.setSelected(true);
        rbOnline.setDisable(true);
        // Limpia configuraciones previas
        resetearFormulario();
        // Muestra un alerta indicando al usuario el proceso de carga de ordenes
        Alert alertaCarga = this.mostrarAlertaCarga("Cargando ordenes", "Buscando ordenes de empleado", "Espere un momento...");

        // Ejecuta la carga de ordenes en segundo plano, por fuera de la pantalla.
        // Al finalizar, vuelve al hilo principal para cerrar la alerta o si existen errores, informar.
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1250);
                Platform.runLater(() -> {
                    try {
                        gestorCierreOrdenInspeccion.nuevoCierreOrdenInspeccion();
                        // Una vez completado, cerrar alerta y actualizar estado
                        alertaCarga.close();
                    } catch (Exception e) {
                        alertaCarga.close();
                        mostrarAlerta("Error", "No se pudieron cargar las órdenes de inspección. Intente nuevamente.");
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    // Recibe los datos del gestor y los procesa en una tabla para poder seleccionar la orden
    public void mostrarDatosOrdenesinspeccion(List<OrdenInspeccionDTO> ordenesInspeccion) {
        // Valida que existan ordenes cargadas
        if (ordenesInspeccion != null && !ordenesInspeccion.isEmpty()) {
            tablaOrdenes.setItems(FXCollections.observableArrayList((ordenesInspeccion)));
            this.solicitarSeleccionOrdenInspeccion();
        } else {
            mostrarAlerta("Advertencia", "No hay ordenes de inspeccion disponibles");
            volverAlInicio();
        }
    }

    // Configura un listener para que el usuario pueda seleccinar la orden de la tabla
    public void solicitarSeleccionOrdenInspeccion() {
        if (lblOrdenInspeccion != null) {
            lblOrdenInspeccion.setVisible(true);
            lblOrdenInspeccion.setManaged(true);
        }
        tablaOrdenes.getSelectionModel().selectedItemProperty().addListener(
                // Una vez seleccionada una orden, se toma la seleccion del usuario
                (observable, oldValue, newValue) -> tomarSeleccionOrdenInspeccion(newValue)
        );
    }

    public void tomarSeleccionOrdenInspeccion(OrdenInspeccionDTO ordenInspeccion) {
        if (ordenInspeccion != null) {
            // Llama al gestor pasando la referencia a la orden seleccionada
            this.gestorCierreOrdenInspeccion.tomarSeleccionOrdenInspeccion(ordenInspeccion);
            // establece el identificador del sismografo a modo de visualizacion.
            // El id se obtiene del objeto de datos no de la entidad
            this.lblIdSismografo.setText(Integer.toString(ordenInspeccion.getIdentificadorSismografo()));
        }
    }

    // Solicita al usuario que ingrese una observacion
    // Configura un listener para cuando el usuario ingresa un texto
    public void solicitarObservacion() {
        seccionObservacion.setDisable(false);
        seccionObservacion.getStyleClass().removeAll("section-disabled");
        seccionObservacion.getStyleClass().add("section-enabled");
        txtObservacion.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                        this.tomarObservacion(txtObservacion.getText());
                }
        );
    }

    public void tomarObservacion(String observacion) {
        this.gestorCierreOrdenInspeccion.tomarObservacion(observacion);
        // Habilita la seccion completa de motivos para modificar el estado del sismografo
        if (seccionMotivos != null) {
            seccionMotivos.setDisable(false);
            seccionMotivos.setManaged(true);
            seccionMotivos.getStyleClass().removeAll("section-disabled");
            seccionMotivos.getStyleClass().add("section-enabled");
        }
    }

    // Solicita seleccionar el motivo tipo por el cual el sismografo se establecera como fuera de servicio
    public void solicitarSeleccionMotivoTipo(List<MotivoTipoDTO> motivosTipoDTO) {
        if (seccionMotivos.isDisabled()) {
            for (MotivoTipoDTO motivo : motivosTipoDTO) {
                // Crea checkbox para poder seleccionar el motivo
                CheckBox checkBox = new CheckBox(motivo.getDescripcion());
                // Crea un area de texto para incluir el comentario
                TextArea textArea = new TextArea();
                textArea.setDisable(true);
                textArea.setPromptText("Ingrese comentario para: " + motivo.getDescripcion());
                textArea.setPrefRowCount(2);
                // CheckBox - selección del motivo
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    // En el caso que se elimine la seleccion, se limpia el texto
                    textArea.setDisable(!newVal);
                    textArea.clear();
                    this.tomarSeleccionMotivoTipo(motivo, newVal);
                });
                // TextArea - ingreso de comentario - Cuando sale del foco del area de texto
                textArea.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                    // Valida que no se este seleccionado el area de texto y que el checbox este seleccionado
                    if (!isFocused && checkBox.isSelected()) {
                        String texto = textArea.getText().trim();
                        // Se debe mantener asociado el motivo con su comentario
                        this.tomarComentario(motivo, texto);
                    }
                });
                // Crea un contenedor para incluir los motivos agregads
                VBox motivoSeccion = new VBox(8);
                motivoSeccion.getChildren().addAll(checkBox, textArea);

                // Se agregan al contenedor principal de la pantalla
                vboxMotivosFuera.getChildren().add(motivoSeccion);
                motivoSeccion.getStyleClass().add("motivo-seccion");
                checkBox.getStyleClass().add("checkbox-bold");
                textArea.getStyleClass().add("text-style");
            }
        }
    }

    // Llama al gestor enviando los motivos seleccionados, validando tambien que efectivamente este seleccionado
    // y no haya sido eliminada esta seleccion posteriormente
    public void tomarSeleccionMotivoTipo(MotivoTipoDTO motivoTipoDTO, Boolean seleccionado) {
        gestorCierreOrdenInspeccion.tomarSeleccionMotivoTipo(motivoTipoDTO, seleccionado);
    }

    // Llama al gestor enviando los motivos seleccionados, junto con los comentarios incluidos
    // asociados a cada motivo tipo
    public void tomarComentario(MotivoTipoDTO motivoTipoDTO, String comentario) {
        gestorCierreOrdenInspeccion.tomarComentario(motivoTipoDTO, comentario);
    }

    /**
     * Muestra una alerta de confirmación para que el usuario confirme el cierre de la orden de inspección.
     * Luego delega la respuesta a un metodo encargado de procesar el cierre de la orden.
     */

    public void solicitarConfirmacionCierreOrden() {
        Alert seleccion = createConfirmationAlert(
                "Confirmar Cierre",
                "¿Está seguro de cerrar la orden de inspección?",
                "Esta acción no se puede deshacer. El sismógrafo será marcado como fuera de servicio."
        );
        this.tomarConfirmacionCierreOrden(seleccion);
    }

    /**
     * Muestra la alerta de confirmación y procesa la respuesta del usuario.
     * Si el usuario confirma, se solicita al gestor que confirme el cierre de la orden.
     */
    public void tomarConfirmacionCierreOrden(Alert seleccion) {
        // Mostrar la alerta y esperar la respuesta del usuario
        seleccion.showAndWait().ifPresent(response -> {
            // Verificar si el usuario presionó el botón aceptar
            if (response == ButtonType.OK) {
                Alert alertProceso = this.mostrarAlertaCarga("Cerrando orden", "Proceso cierre de orden", "Procesando cierre de orden, espere un momento...");
                // Realiza el proceso de cierre en segundo plano
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> {
                            try {
                                // Invoca al gestor que se encargara de que las validaciones es cumplan
                                gestorCierreOrdenInspeccion.tomarConfirmacionCierreOrden();
                                alertProceso.close();
                                mostrarAlerta("Exito", "Orden de inspeccion cerrada\n" + "Estado de sismografo actualizado a fuera de servicio" );
                                volverAlInicio();
                            } catch (Exception e) {
                                alertProceso.close();
                                mostrarAlerta("Error", e.getMessage());
                            }
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
    }

    // Inicaliza componentes FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            inicializarComponentes();
            configurarTabla();
            LOGGER.info("Controlador inicializado correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error durante la inicialización", e);
            mostrarAlerta("Error", "Error durante la inicialización: " + e.getMessage());
        }
    }

    private void inicializarComponentes() {
        // Inicializar estado inicial
        panelBienvenida.setVisible(true);
        scrollInspeccion.setVisible(false);

        // Configurar el toggle para el estado del sismografo
        estadoToggleGroup = new ToggleGroup();
        rbOnline.setToggleGroup(estadoToggleGroup);
        rbFueraServicio.setToggleGroup(estadoToggleGroup);

        // Estado inicial de las secciones
        resetearEstadoSecciones();

        // Configuracion del listener cuando se marca a fuera de servicio
        rbFueraServicio.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    vboxMotivosFuera.setVisible(newValue);
                    vboxMotivosFuera.setManaged(newValue);
                }
        );

        buttonConfirmar.setOnAction(event -> solicitarConfirmacionCierreOrden());

        LOGGER.info("Componentes inicializados");
    }

    private void configurarTabla() {
        try {
            // Configurar columnas de la tabla
            tablaOrdenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            colNumOrden.setCellValueFactory(new PropertyValueFactory<>("numeroOrden"));
            colFechaFinalizacion.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
            colEstacion.setCellValueFactory(new PropertyValueFactory<>("nombreEstacion"));
            colSismografo.setCellValueFactory(new PropertyValueFactory<>("identificadorSismografo"));

            LOGGER.info("Tabla configurada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error configurando tabla", e);
        }
    }

    // Cuando el usuario presiona el boton para cancelar el proceso, se redirige al menu de inicio
    @FXML
    private void cancelarProceso() {
        Alert confirmacion = createConfirmationAlert(
                "Cancelar Proceso",
                "Esta accion finaliza el proceso, Desea continuar?",
                "Se perderán todos los datos ingresados."
        );

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                volverAlInicio();
                LOGGER.info("Proceso cancelado por el usuario");
            }
        });
    }

    // Cuando el usuario presiona el boton para salir, el programa se cerrara
    @FXML
    private void salir() {
        Alert alert = createConfirmationAlert(
                "Confirmar Salida",
                "¿Estás seguro que deseas salir?",
                "Se cerrará la aplicación."
        );
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    // Metodos de soporte para restablecer valores de componentes y estilos
    private void resetearEstadoSecciones() {
        // Deshabilitar todas las secciones excepto la tabla
        seccionObservacion.setDisable(true);
        seccionMotivos.setDisable(true);
        vboxMotivosFuera.setVisible(false);
        vboxMotivosFuera.setManaged(false);

        // Aplicar estilo deshabilitado
        seccionObservacion.getStyleClass().removeAll("section-enabled");
        seccionObservacion.getStyleClass().add("section-disabled");
        seccionMotivos.getStyleClass().removeAll("section-enabled");
        seccionMotivos.getStyleClass().add("section-disabled");
    }

    private void resetearFormulario() {
        // Limpiar selección de tabla
        tablaOrdenes.getSelectionModel().clearSelection();
        tablaOrdenes.getItems().clear();

        // Limpiar observación
        txtObservacion.clear();

        // Limpiar el contenedor visual
        vboxMotivosFuera.getChildren().clear();
        seccionMotivos.setDisable(true);

        // Resetear estado de secciones
        resetearEstadoSecciones();
    }

    private void volverAlInicio() {
        scrollInspeccion.setVisible(false);
        panelBienvenida.setVisible(true);
        resetearFormulario();
    }

    // Crea una ventana de alerta para que el usuario decida sobre una accion
    private Alert createConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    // Crear una ventana de alerta solo para informar al usuario cuando se completo alguna accion o hubo errores
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Crea una ventana de alerta informando un proceso que se encuentra en curso y el usuario debe esperar a que se complete
    private Alert mostrarAlertaCarga(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        alert.show();
        return alert;
    }
}