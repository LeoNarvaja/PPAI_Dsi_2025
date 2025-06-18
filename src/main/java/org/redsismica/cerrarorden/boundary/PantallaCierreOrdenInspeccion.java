package org.redsismica.cerrarorden.boundary;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.redsismica.cerrarorden.control.GestorCierreOrdenInspeccion;
import org.redsismica.cerrarorden.dtos.OrdenInspeccionDTO;
import org.redsismica.cerrarorden.entities.MotivoTipo;
import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.entities.Sismografo;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PantallaCierreOrdenInspeccion implements Initializable {

    private GestorCierreOrdenInspeccion gestorCierreOrdenInspeccion;
    private Map<CheckBox, MotivoTipo> motivoTipoMap = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(PantallaCierreOrdenInspeccion.class.getName());

    private static final String SECTION_ENABLED_STYLE =
            "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 12; " +
                    "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                    "-fx-opacity: 1.0;";

    private static final String SECTION_DISABLED_STYLE =
            "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 12; " +
                    "-fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                    "-fx-opacity: 0.6;";

    @FXML private VBox panelBienvenida;
    @FXML private ScrollPane scrollInspeccion;
    @FXML private Label lblUsuario;
    @FXML private Label lblSistema;
    @FXML private Label lblFechaHora;

    @FXML private TableView<OrdenInspeccionDTO> tablaOrdenes;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colNumOrden;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colFechaFinalizacion;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colEstacion;
    @FXML private TableColumn<OrdenInspeccionDTO, String> colSismografo;

    @FXML private VBox seccionObservacion;
    @FXML private VBox seccionMotivos;
    @FXML private VBox seccionConfirmacion;

    @FXML private Label lblIdSismografo;
    @FXML private RadioButton rbOnline;
    @FXML private RadioButton rbFueraServicio;
    @FXML private VBox vboxMotivosFuera;
    @FXML private HBox labelSeleccionOrden;

    @FXML private TextArea txtObservacion;

    @FXML private Label lblResumenOrden;
    @FXML private Label lblResumenObservacion;
    @FXML private Label lblResumenMotivos;

    private final ArrayList<CheckBox> motivosData = new ArrayList<>();
    private final ArrayList<TextArea> textAreas = new ArrayList<>();
    private Timer timerReloj;
    private ToggleGroup estadoToggleGroup;

    // ============= Constructor de la clase pantalla =============
    public PantallaCierreOrdenInspeccion() {
        this.gestorCierreOrdenInspeccion = new GestorCierreOrdenInspeccion(this);
    }

    @FXML
    private void opcDarCierreOrdenInspeccion() {
        try {
            this.habilitarPantalla();
            LOGGER.info("Pantalla habilitada");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error mostrando la panatalla", e);
        }
    }

    private void habilitarPantalla() {
        panelBienvenida.setVisible(false);
        scrollInspeccion.setVisible(true);
        if (seccionConfirmacion != null) {
            seccionConfirmacion.setVisible(true);
        }
        scrollInspeccion.setVvalue(0);
        rbOnline.setSelected(true);
        rbOnline.setDisable(true);
        resetearFormulario();
        lblSistema.setText("Panel de inspección activado");
        actualizarResumen();
        this.gestorCierreOrdenInspeccion.reiniciarDatos();
        Alert alertaCarga = this.mostrarAlertaCarga("Cargando ordenes", "Buscando ordenes de empleado", "Espere un momento...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1250);
                Platform.runLater(() -> {
                    try {
                        gestorCierreOrdenInspeccion.nuevoCierreOrdenInspeccion();
                        // Una vez completado, cerrar alerta y actualizar estado
                        alertaCarga.close();
                        lblSistema.setText("Órdenes cargadas - Seleccione una orden para continuar");
                        actualizarResumen();
                    } catch (IOException e) {
                        alertaCarga.close();
                        mostrarAlerta("Error", "No se pudieron cargar las órdenes de inspección. Intente nuevamente.");
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    public void mostrarDatosOrdenesinspeccion(List<OrdenInspeccionDTO> ordenes) {
        if (ordenes != null && !ordenes.isEmpty()) {
            ObservableList<OrdenInspeccionDTO> ordenesEmpleado = FXCollections.observableArrayList(ordenes
            );
            tablaOrdenes.setItems(ordenesEmpleado);

            this.solicitarSeleccionOrdenInspeccion();
        } else {
            mostrarAlerta("Advertencia", "No hay ordenes de inspeccion disponibles");
            volverAlInicio();
        }
    }

    public void solicitarSeleccionOrdenInspeccion() {
        if (labelSeleccionOrden != null) {
            labelSeleccionOrden.setVisible(true);
            labelSeleccionOrden.setManaged(true);
        }
    }

    public void tomarSeleccionOrdenInspeccion(OrdenInspeccionDTO orden) {
        if (orden != null) {
            this.gestorCierreOrdenInspeccion.tomarSeleccionOrdenInspeccion(orden);
            actualizarResumen();
        }
    }

    public void solicitarObservacion() {
        seccionObservacion.setDisable(false);
        seccionObservacion.setStyle(SECTION_ENABLED_STYLE);
        txtObservacion.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                        this.gestorCierreOrdenInspeccion.tomarObservacion(txtObservacion.getText());
                        onObservacionCambiada();
                }
        );
    }

    public void solicitarSeleccionMotivoTipo(List<MotivoTipo> motivosTipos) {
        // Solo crear los controles si aún no existen ()
        if (motivoTipoMap.isEmpty()) {
            for (MotivoTipo motivoTipo : motivosTipos) {
                CheckBox checkbox = new CheckBox(motivoTipo.getDescripcion());
                checkbox.setId("chk" + motivoTipo.getDescripcion());
                this.motivosData.add(checkbox);

                TextArea textArea = new TextArea();
                textArea.setId("txtComentario" + motivoTipo.getDescripcion());
                textArea.setPromptText("Ingrese comentario para " + motivoTipo.getDescripcion());
                textArea.setDisable(true);
                textArea.setPrefRowCount(2);

                this.textAreas.add(textArea);
                VBox motivoSeccion = new VBox(8);
                motivoSeccion.getChildren().addAll(checkbox, textArea);

                vboxMotivosFuera.getChildren().add(motivoSeccion);
                motivoSeccion.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 8");
                checkbox.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                textArea.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px;");
                motivoTipoMap.put(checkbox, motivoTipo);
            }
        }
        this.tomarSeleccionMotivosTipos();
    }

    public void tomarSeleccionMotivosTipos() {
        // Mapa para almacenar motivos seleccionados asociados con sus comentarios
        Map<MotivoTipo, String> motivosConComentarios = new HashMap<>();

        // Recorremos cada entrada del mapa que vincula CheckBox con MotivoTipo
        for (Map.Entry<CheckBox, MotivoTipo> entry : motivoTipoMap.entrySet()) {
            CheckBox checkbox = entry.getKey();
            MotivoTipo motivoTipo = entry.getValue();

            // Obtener el índice del checkbox para acceder al TextArea correspondiente
            int index = motivosData.indexOf(checkbox);
            TextArea textArea = textAreas.get(index);

            // Listener para cambios en la selección del checkbox
            checkbox.selectedProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        // Habilita o deshabilita el TextArea según el estado del checkbox
                        textArea.setDisable(!newValue);
                        if (!newValue) {
                            // Si se desmarca, limpiar el texto y eliminar el motivo del mapa
                            textArea.clear();
                            motivosConComentarios.remove(motivoTipo);
                        } else {
                            // Si se marca, agregar con comentario vacío inicialmente
                            motivosConComentarios.put(motivoTipo, "");
                        }

                        // Notificar al gestor con la selección actualizada
                        this.gestorCierreOrdenInspeccion.tomarSeleccionMotivosTipo(motivosConComentarios);
                        actualizarResumen();
                    }
            );

            // Listener para cuando el TextArea correspondiente al comentario pierde el foco
            textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
                // Solo se actúa si pierde el foco y el checkbox está seleccionado
                if (!newValue && checkbox.isSelected()) {
                    // Actualizar el comentario en el mapa
                    String comentario = textArea.getText().trim();
                    motivosConComentarios.put(motivoTipo, comentario);

                    // Notificar al gestor con los comentarios actualizados
                    this.gestorCierreOrdenInspeccion.tomarSeleccionMotivosTipo(motivosConComentarios);
                }
                actualizarResumen();
            });
        }
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
     *
     * @param seleccion Objeto Alert que representa el diálogo de confirmación.
     */
    public void tomarConfirmacionCierreOrden(Alert seleccion) {
        // Mostrar la alerta y esperar la respuesta del usuario
        seleccion.showAndWait().ifPresent(response -> {
            // Verificar si el usuario presionó el botón aceptar
            if (response == ButtonType.OK) {
                lblSistema.setText("Procesando cierre de orden...");
                Alert alertProceso = this.mostrarAlertaCarga("Cerrando orden", "Proceso cierre de orden", "Procesando cierre de orden, espere un momento...");
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            try {
                                gestorCierreOrdenInspeccion.tomarConfirmacionCierreOrden();
                                alertProceso.close();
                                mostrarAlerta("Exito", "Orden de inspeccion cerrada\n" + "Estado de sismografo actualizado a fuera de servicio" );
                                actualizarResumen();
                                lblSistema.setText("Orden procesada con exito");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            inicializarComponentes();
            configurarTabla();
            configurarListeners();
            iniciarReloj();
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

        LOGGER.info("Componentes inicializados");
    }

    private void configurarTabla() {
        try {
            // Configurar columnas de la tabla
            tablaOrdenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            colNumOrden.setCellValueFactory(new PropertyValueFactory<>("numeroOrden"));
            colFechaFinalizacion.setCellValueFactory(new PropertyValueFactory<>("fechaFinalizacion"));
            colEstacion.setCellValueFactory(new PropertyValueFactory<>("estacion"));
            colSismografo.setCellValueFactory(new PropertyValueFactory<>("identificadorSismografo"));

            LOGGER.info("Tabla configurada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error configurando tabla", e);
        }
    }

    private void configurarListeners() {
        // Listener para cambio de estado del sismógrafo
        rbFueraServicio.selectedProperty().addListener(
                (observable, oldValue, newValue) -> onEstadoSismografoCambiado(newValue)
        );

        tablaOrdenes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tomarSeleccionOrdenInspeccion(newValue)
        );

    }

    private void onMotivoSeleccionado(CheckBox motivo, boolean selected) {
        actualizarResumen();
    }

    private void onObservacionCambiada() {
        habilitarSeccionMotivos();
        actualizarResumen();
    }

    private void onEstadoSismografoCambiado(boolean fueraDeServicio) {
        vboxMotivosFuera.setVisible(fueraDeServicio);
        vboxMotivosFuera.setManaged(fueraDeServicio);
        actualizarResumen();
    }

    private void iniciarReloj() {
        timerReloj = new Timer(true);
        timerReloj.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"));
                    lblFechaHora.setText(fechaHora);
                });
            }
        }, 0, 1000);
    }

    public void cargarDatosIniciales(String usuario) {
        lblUsuario.setText(usuario);
        lblSistema.setText("Sistema conectado - Listo para procesar órdenes");
    }

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

    @FXML
    private void salir() {
        Alert alert = createConfirmationAlert(
                "Confirmar Salida",
                "¿Estás seguro que deseas salir?",
                "Se cerrará la aplicación."
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cleanup();
                Platform.exit();
            }
        });
    }

    // ============= Métodos de Navegación y Estado =============

    private void habilitarSeccionMotivos() {
        if (seccionMotivos != null) {
            seccionMotivos.setDisable(false);
            seccionMotivos.setStyle(SECTION_ENABLED_STYLE);
        }
    }

    private void resetearEstadoSecciones() {
        // Deshabilitar todas las secciones excepto la tabla
        seccionObservacion.setDisable(true);
        seccionMotivos.setDisable(true);
        vboxMotivosFuera.setVisible(false);
        vboxMotivosFuera.setManaged(false);

        // Aplicar estilo deshabilitado
        seccionObservacion.setStyle(SECTION_DISABLED_STYLE);
        seccionMotivos.setStyle(SECTION_DISABLED_STYLE);
    }

    private void actualizarResumen() {
        actualizarResumenOrden();
        actualizarResumenObservacion();
        actualizarResumenMotivos();
    }

    private void actualizarResumenOrden() {
        if (tablaOrdenes.getSelectionModel().getSelectedItem() == null) {
            setLabelWithStatus(lblResumenOrden, "❌" + " Sin orden seleccionada", Color.RED);
        } else {
            setLabelWithStatus(lblResumenOrden, "✅" + " Orden seleccionada", Color.rgb(30, 60, 114));
        }
    }

    private void actualizarResumenObservacion() {
        String textoObservacion = txtObservacion.getText().trim();
        if (textoObservacion.isEmpty()) {
            setLabelWithStatus(lblResumenObservacion, "❌" + " Observación no ingresada", Color.RED);
        } else {
            setLabelWithStatus(lblResumenObservacion, "✅" + " Observación ingresada", Color.rgb(30, 60, 114));
        }
    }

    private void actualizarResumenMotivos() {
        long motivosSeleccionados = motivosData.stream()
                .mapToLong(motivo -> motivo.isSelected() ? 1 : 0)
                .sum();

        if (motivosSeleccionados == 0) {
            setLabelWithStatus(lblResumenMotivos, "❌" + " Sin motivos seleccionados", Color.RED);
        } else {
            setLabelWithStatus(lblResumenMotivos, "✅" + " Motivos seleccionados: " + motivosSeleccionados, Color.rgb(30, 60, 114));
        }
    }

    private void setLabelWithStatus(Label label, String text, Color color) {
        label.setText(text);
        label.setTextFill(color);
    }

    private void resetearFormulario() {
        // Limpiar selección de tabla
        tablaOrdenes.getSelectionModel().clearSelection();
        tablaOrdenes.getItems().clear();
        //ordenSeleccionada = null;

        // Limpiar observación
        txtObservacion.clear();

        // Limpiar estados de los componentes antes de eliminarlos
        for (CheckBox checkbox : motivosData) {
            checkbox.setSelected(false);
        }

        for (TextArea textArea : textAreas) {
            textArea.clear();
            textArea.setDisable(true);
        }

        // Limpiar el contenedor visual
        vboxMotivosFuera.getChildren().clear();
        motivoTipoMap.clear();

        // Resetear estado de secciones
        resetearEstadoSecciones();
    }

    private void volverAlInicio() {
        this.gestorCierreOrdenInspeccion.reiniciarDatos();
        scrollInspeccion.setVisible(false);
        panelBienvenida.setVisible(true);
        seccionConfirmacion.setVisible(false);
        resetearFormulario();
        lblSistema.setText("Sistema conectado - Listo para procesar órdenes");
    }

    private Alert createConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Alert mostrarAlertaCarga(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        alert.show();
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);
        alert.setGraphic(spinner);
        alert.show();
        return alert;
    }

    private void cleanup() {
        if (timerReloj != null) {
            timerReloj.cancel();
            timerReloj = null;
        }
        LOGGER.info("Recursos liberados correctamente");
    }
}