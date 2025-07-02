package org.example.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.*;
import org.example.service.EvaluatorsService;
import org.example.service.GeneratorService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML private Spinner<Integer> spinnerCantidad;
    @FXML private Button btnGenerar;
    @FXML private Button btnEvaluarSecuencial;
    @FXML private Button btnEvaluarParalelo;
    @FXML private Button btnLimpiar;

    @FXML private TableView<PostulanteEvaluacion> tablePostulantes;
    @FXML private TableColumn<PostulanteEvaluacion, String> colCodigo;
    @FXML private TableColumn<PostulanteEvaluacion, String> colNombre;
    @FXML private TableColumn<PostulanteEvaluacion, String> colEscuela;
    @FXML private TableColumn<PostulanteEvaluacion, String> colProceso;
    @FXML private TableColumn<PostulanteEvaluacion, Double> colPuntaje;
    @FXML private TableColumn<PostulanteEvaluacion, String> colEstado;
    @FXML private TableColumn<PostulanteEvaluacion, Long> colTiempo;

    @FXML private ProgressBar progressBar;
    @FXML private Label lblProgreso;
    @FXML private Label lblEstadisticas;

    // Labels para estadísticas detalladas
    @FXML private Label lblPromedioGeneral;
    @FXML private Label lblMejorPuntaje;
    @FXML private Label lblTiempoPromedio;
    @FXML private Label lblTasaAprobacion;

    @FXML private BarChart<String, Number> chartResultados;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // ComboBox para filtrar por escuela y proceso
    @FXML private ComboBox<String> cmbEscuela;
    @FXML private ComboBox<String> cmbProceso;

    private final ObservableList<PostulanteEvaluacion> listaPostulantes = FXCollections.observableArrayList();
    private Task<?> tareaActual;

    // Gabarito para evaluación (se puede generar o cargar)
    private String gabaritoRespuestas;

    // Clase interna para manejar la evaluación con JavaFX Properties
    public static class PostulanteEvaluacion {
        private final Postulante postulante;
        private final ResultadoExamen resultado;
        private final Respuestas respuestas;
        private String estado = "Pendiente";
        private long tiempoEvaluacion = 0;

        public PostulanteEvaluacion(Postulante postulante) {
            this.postulante = postulante;
            this.resultado = new ResultadoExamen(postulante.getCodigoPostulante());
            this.respuestas = new Respuestas(postulante.getCodigoPostulante(), "");
            this.resultado.setPostulante(postulante);
            this.respuestas.setPostulante(postulante);
        }

        // Getters para la tabla
        public String getCodigo() { return postulante.getCodigoPostulante(); }
        public String getNombre() { return postulante.getNombre(); }
        public String getEscuela() { return postulante.getNombreEscuela(); }
        public String getProceso() { return postulante.getProcesoDescripcion(); }
        public Double getPuntaje() { return resultado.getPuntaje(); }
        public String getEstado() { return estado; }
        public Long getTiempo() { return tiempoEvaluacion; }

        // Setters
        public void setEstado(String estado) { this.estado = estado; }
        public void setTiempoEvaluacion(long tiempo) { this.tiempoEvaluacion = tiempo; }
        public void setPuntaje(double puntaje) { resultado.setPuntaje(puntaje); }

        public Postulante getPostulante() { return postulante; }
        public ResultadoExamen getResultado() { return resultado; }
        public Respuestas getRespuestas() { return respuestas; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarSpinner();
        configurarChart();
        configurarComboBoxes();
        configurarEventHandlers();
        inicializarEstado();

        // Generar gabarito aleatorio
        gabaritoRespuestas = generarGabaritoAleatorio();
        lblProgreso.setText("Sistema listo - Gabarito generado");
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEscuela.setCellValueFactory(new PropertyValueFactory<>("escuela"));
        colProceso.setCellValueFactory(new PropertyValueFactory<>("proceso"));
        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntaje"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));

        // Configurar formato de columnas
        colPuntaje.setCellFactory(column -> new TableCell<PostulanteEvaluacion, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        colTiempo.setCellFactory(column -> new TableCell<PostulanteEvaluacion, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0) {
                    setText(null);
                } else {
                    setText(item + "ms");
                }
            }
        });

        // Colorear filas según el estado
        tablePostulantes.setRowFactory(tv -> new TableRow<PostulanteEvaluacion>() {
            @Override
            protected void updateItem(PostulanteEvaluacion item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getEstado()) {
                        case "Aprobado":
                            setStyle("-fx-background-color: #d4edda;");
                            break;
                        case "Desaprobado":
                            setStyle("-fx-background-color: #f8d7da;");
                            break;
                        case "Evaluando":
                            setStyle("-fx-background-color: #fff3cd;");
                            break;
                        case "No se presentó":
                            setStyle("-fx-background-color: #f1f3f4;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        tablePostulantes.setItems(listaPostulantes);
    }

    private void configurarSpinner() {
        // Configurar spinner con valores más amplios y editable
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 50);
        valueFactory.setWrapAround(false);
        spinnerCantidad.setValueFactory(valueFactory);

        // Hacer el spinner editable
        spinnerCantidad.setEditable(true);

        // Agregar listener para validar entrada manual
        spinnerCantidad.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinnerCantidad.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Commit del valor cuando se pierde el foco
        spinnerCantidad.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                spinnerCantidad.increment(0); // Esto fuerza el commit del valor
            }
        });
    }

    private void configurarChart() {
        chartResultados.setTitle("Resultados de Evaluación - Examen de Admisión UNMSM");
        xAxis.setLabel("Estado de Postulantes");
        yAxis.setLabel("Cantidad");
        chartResultados.setLegendVisible(false);
        chartResultados.setAnimated(false); // Desactivar animaciones para mejor rendimiento
    }

    private void configurarComboBoxes() {
        // Configurar ComboBox de escuelas
        cmbEscuela.getItems().addAll(
                "Todas las Escuelas",
                "Medicina Humana",
                "Ingeniería de Sistemas e Informática",
                "Derecho y Ciencia Política",
                "Administración",
                "Contabilidad",
                "Psicología",
                "Educación",
                "Medicina Veterinaria",
                "Odontología",
                "Farmacia y Bioquímica"
        );
        cmbEscuela.setValue("Todas las Escuelas");

        // Configurar ComboBox de procesos
        cmbProceso.getItems().addAll(
                "Todos los Procesos",
                "2024-I",
                "2024-II",
                "2025-I"
        );
        cmbProceso.setValue("Todos los Procesos");

        // Agregar listeners para filtrado
        cmbEscuela.setOnAction(e -> filtrarPostulantes());
        cmbProceso.setOnAction(e -> filtrarPostulantes());
    }

    private void configurarEventHandlers() {
        btnGenerar.setOnAction(e -> generarPostulantes());
        btnEvaluarSecuencial.setOnAction(e -> iniciarEvaluacionSecuencial());
        btnEvaluarParalelo.setOnAction(e -> iniciarEvaluacionParalelo());
        btnLimpiar.setOnAction(e -> limpiarDatos());
    }

    private void inicializarEstado() {
        // Deshabilitar botones de evaluación al inicio
        btnEvaluarSecuencial.setDisable(true);
        btnEvaluarParalelo.setDisable(true);

        // Limpiar estadísticas detalladas
        lblPromedioGeneral.setText("--");
        lblMejorPuntaje.setText("--");
        lblTiempoPromedio.setText("--");
        lblTasaAprobacion.setText("--");

        // Limpiar estadísticas generales
        lblEstadisticas.setText("");

        // Reiniciar progress bar
        progressBar.setProgress(0);
    }

    @FXML
    private void generarPostulantes() {
        try {
            // Obtener cantidad del spinner con validación
            Integer cantidad = spinnerCantidad.getValue();
            if (cantidad == null || cantidad <= 0) {
                mostrarAlerta("Error", "Por favor ingrese una cantidad válida de postulantes (mayor a 0)");
                return;
            }

            if (cantidad > 10000) {
                mostrarAlerta("Advertencia", "Se recomienda no generar más de 10,000 postulantes para mejor rendimiento");
            }

            // Cancelar tarea anterior si existe
            if (tareaActual != null && tareaActual.isRunning()) {
                tareaActual.cancel();
            }

            List<Postulante> nuevosPostulantes = generarPostulantesPrueba(cantidad);

            listaPostulantes.clear();
            for (Postulante postulante : nuevosPostulantes) {
                PostulanteEvaluacion pe = new PostulanteEvaluacion(postulante);
                // Usar tu GeneratorService para generar respuestas aleatorias
                String respuestasAleatorias = GeneratorService.generarRespuestasAleatorias();
                pe.getRespuestas().setRespuesta(respuestasAleatorias);
                listaPostulantes.add(pe);
            }

            actualizarChart();
            lblProgreso.setText("Generados " + cantidad + " postulantes con respuestas aleatorias");
            lblEstadisticas.setText("Total: " + cantidad + " | Pendientes: " + cantidad);

            // Habilitar botones de evaluación
            btnEvaluarSecuencial.setDisable(false);
            btnEvaluarParalelo.setDisable(false);

        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al generar postulantes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void iniciarEvaluacionSecuencial() {
        if (listaPostulantes.isEmpty()) {
            mostrarAlerta("Error", "No hay postulantes para evaluar");
            return;
        }

        prepararEvaluacion();
        tareaActual = crearTareaEvaluacionSecuencial();
        configurarTarea(tareaActual);

        Thread thread = new Thread(tareaActual);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void iniciarEvaluacionParalelo() {
        if (listaPostulantes.isEmpty()) {
            mostrarAlerta("Error", "No hay postulantes para evaluar");
            return;
        }

        prepararEvaluacion();
        tareaActual = crearTareaEvaluacionParalela();
        configurarTarea(tareaActual);

        Thread thread = new Thread(tareaActual);
        thread.setDaemon(true);
        thread.start();
    }

    private Task<Void> crearTareaEvaluacionSecuencial() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long tiempoInicio = System.currentTimeMillis();

                for (int i = 0; i < listaPostulantes.size(); i++) {
                    if (isCancelled()) break;

                    PostulanteEvaluacion pe = listaPostulantes.get(i);
                    evaluarPostulante(pe);

                    final int indice = i;
                    Platform.runLater(() -> {
                        updateProgress(indice + 1, listaPostulantes.size());
                        lblProgreso.setText(String.format("Evaluación Secuencial: %d/%d - %s",
                                indice + 1, listaPostulantes.size(), pe.getNombre()));
                        actualizarEstadisticas();
                        tablePostulantes.refresh();
                    });

                    Thread.sleep(50); // Pausa para visualizar progreso
                }

                long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
                Platform.runLater(() -> {
                    lblProgreso.setText(String.format("Evaluación secuencial completada en %.2f segundos",
                            tiempoTotal / 1000.0));
                });

                return null;
            }
        };
    }

    private Task<Void> crearTareaEvaluacionParalela() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long tiempoInicio = System.currentTimeMillis();

                // Usar parallel streams para evaluación paralela
                listaPostulantes.parallelStream().forEach(pe -> {
                    if (!isCancelled()) {
                        evaluarPostulante(pe);
                        Platform.runLater(() -> {
                            // Calcular progreso basado en completados
                            long completados = listaPostulantes.stream()
                                    .mapToLong(p -> !"Evaluando".equals(p.getEstado()) &&
                                            !"Pendiente".equals(p.getEstado()) ? 1 : 0).sum();
                            updateProgress(completados, listaPostulantes.size());
                            lblProgreso.setText(String.format("Evaluación Paralela: %d/%d completados",
                                    completados, listaPostulantes.size()));
                            actualizarEstadisticas();
                            tablePostulantes.refresh();
                        });
                    }
                });

                long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
                Platform.runLater(() -> {
                    updateProgress(listaPostulantes.size(), listaPostulantes.size());
                    double mejora = (double) listaPostulantes.size() * 1000.0 / tiempoTotal;
                    lblProgreso.setText(String.format("Evaluación paralela completada en %.2f segundos (%.1fx más eficiente)",
                            tiempoTotal / 1000.0, mejora));
                });

                return null;
            }
        };
    }

    private void evaluarPostulante(PostulanteEvaluacion pe) {
        try {
            long tiempoInicio = System.currentTimeMillis();

            // Simular tiempo de procesamiento
            Thread.sleep(java.util.concurrent.ThreadLocalRandom.current().nextInt(500, 1500));

            Respuestas respuestas = pe.getRespuestas();
            ResultadoExamen resultado = pe.getResultado();

            if (respuestas.getRespuesta() != null && !respuestas.getRespuesta().isEmpty()) {
                // Usar tu EvaluatorsService para calcular puntaje
                float puntajeCalculado = EvaluatorsService.calcularPuntaje(
                        respuestas.getRespuesta(), gabaritoRespuestas);

                // Calcular estadísticas de respuestas
                int correctas = respuestas.contarRespuestasCorrectas(gabaritoRespuestas);
                int incorrectas = respuestas.contarRespuestasIncorrectas(gabaritoRespuestas);
                int nulas = respuestas.contarRespuestasNulas();

                long tiempoFinal = System.currentTimeMillis() - tiempoInicio;

                Platform.runLater(() -> {
                    // Actualizar resultado
                    resultado.setPuntaje((double) puntajeCalculado);
                    resultado.setRespuestasCorrectas(correctas);
                    resultado.setRespuestasIncorrectas(incorrectas);
                    resultado.setRespuestasNulas(nulas);
                    resultado.setFechaEvaluacion(LocalDateTime.now());

                    // Determinar estado y observaciones
                    if (puntajeCalculado >= 11.0) {
                        pe.setEstado("Aprobado");
                        resultado.setObservacion(ResultadoExamen.ObservacionEnum.ALCANZANTE);
                        resultado.setMerito("ALCANZANTE");
                    } else {
                        pe.setEstado("Desaprobado");
                        resultado.setMerito("NO ALCANZANTE");
                    }

                    pe.setTiempoEvaluacion(tiempoFinal);
                });
            } else {
                // Postulante no se presentó
                Platform.runLater(() -> {
                    pe.setEstado("No se presentó");
                    resultado.setObservacion(ResultadoExamen.ObservacionEnum.NO_SE_PRESENTO);
                    resultado.setPuntaje(0.0);
                });
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Platform.runLater(() -> pe.setEstado("Interrumpido"));
        }
    }

    private void prepararEvaluacion() {
        try {
            // Resetear estados
            listaPostulantes.forEach(pe -> {
                pe.setEstado("Evaluando");
                pe.getResultado().setPuntaje(0.0);
                pe.setTiempoEvaluacion(0);
            });

            progressBar.setProgress(0);
            btnEvaluarSecuencial.setDisable(true);
            btnEvaluarParalelo.setDisable(true);
            btnGenerar.setDisable(true);

            tablePostulantes.refresh();
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al preparar evaluación: " + ex.getMessage());
        }
    }

    private void configurarTarea(Task<?> tarea) {
        try {
            progressBar.progressProperty().bind(tarea.progressProperty());

            tarea.setOnSucceeded(e -> finalizarEvaluacion());
            tarea.setOnFailed(e -> {
                finalizarEvaluacion();
                mostrarAlerta("Error", "Error durante la evaluación: " +
                        (tarea.getException() != null ? tarea.getException().getMessage() : "Error desconocido"));
            });
            tarea.setOnCancelled(e -> finalizarEvaluacion());
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al configurar tarea: " + ex.getMessage());
        }
    }

    private void finalizarEvaluacion() {
        Platform.runLater(() -> {
            try {
                // Desvincular progress bar de la tarea
                progressBar.progressProperty().unbind();

                btnEvaluarSecuencial.setDisable(false);
                btnEvaluarParalelo.setDisable(false);
                btnGenerar.setDisable(false);
                actualizarChart();
                actualizarEstadisticas();
                tablePostulantes.refresh();
            } catch (Exception ex) {
                mostrarAlerta("Error", "Error al finalizar evaluación: " + ex.getMessage());
            }
        });
    }

    private void actualizarEstadisticas() {
        try {
            long total = listaPostulantes.size();
            long aprobados = listaPostulantes.stream()
                    .mapToLong(pe -> "Aprobado".equals(pe.getEstado()) ? 1 : 0).sum();
            long desaprobados = listaPostulantes.stream()
                    .mapToLong(pe -> "Desaprobado".equals(pe.getEstado()) ? 1 : 0).sum();
            long noSePresentaron = listaPostulantes.stream()
                    .mapToLong(pe -> "No se presentó".equals(pe.getEstado()) ? 1 : 0).sum();
            long pendientes = total - aprobados - desaprobados - noSePresentaron;

            lblEstadisticas.setText(String.format(
                    "Total: %d | Aprobados: %d (%.1f%%) | Desaprobados: %d (%.1f%%) | No se presentaron: %d | Pendientes: %d",
                    total, aprobados, (total > 0 ? aprobados * 100.0 / total : 0),
                    desaprobados, (total > 0 ? desaprobados * 100.0 / total : 0), noSePresentaron, pendientes));

            actualizarEstadisticasDetalladas();
        } catch (Exception ex) {
            System.err.println("Error actualizando estadísticas: " + ex.getMessage());
        }
    }

    private void actualizarEstadisticasDetalladas() {
        try {
            // Calcular promedio general solo de evaluados
            double promedioGeneral = listaPostulantes.stream()
                    .filter(pe -> pe.getPuntaje() != null && pe.getPuntaje() > 0)
                    .mapToDouble(PostulanteEvaluacion::getPuntaje)
                    .average()
                    .orElse(0.0);

            // Encontrar mejor puntaje
            double mejorPuntaje = listaPostulantes.stream()
                    .filter(pe -> pe.getPuntaje() != null)
                    .mapToDouble(PostulanteEvaluacion::getPuntaje)
                    .max()
                    .orElse(0.0);

            // Calcular tiempo promedio
            double tiempoPromedio = listaPostulantes.stream()
                    .filter(pe -> pe.getTiempo() != null && pe.getTiempo() > 0)
                    .mapToLong(PostulanteEvaluacion::getTiempo)
                    .average()
                    .orElse(0.0);

            // Calcular tasa de aprobación
            long totalEvaluados = listaPostulantes.stream()
                    .mapToLong(pe -> pe.getPuntaje() != null && pe.getPuntaje() > 0 ? 1 : 0).sum();

            long aprobados = listaPostulantes.stream()
                    .mapToLong(pe -> "Aprobado".equals(pe.getEstado()) ? 1 : 0).sum();

            double tasaAprobacion = totalEvaluados > 0 ? (aprobados * 100.0 / totalEvaluados) : 0.0;

            // Actualizar labels de forma segura
            Platform.runLater(() -> {
                lblPromedioGeneral.setText(String.format("%.2f", promedioGeneral));
                lblMejorPuntaje.setText(String.format("%.2f", mejorPuntaje));
                lblTiempoPromedio.setText(String.format("%.0f ms", tiempoPromedio));
                lblTasaAprobacion.setText(String.format("%.1f%%", tasaAprobacion));
            });
        } catch (Exception ex) {
            System.err.println("Error actualizando estadísticas detalladas: " + ex.getMessage());
        }
    }

    private void actualizarChart() {
        try {
            Platform.runLater(() -> {
                long aprobados = listaPostulantes.stream()
                        .mapToLong(pe -> "Aprobado".equals(pe.getEstado()) ? 1 : 0).sum();
                long desaprobados = listaPostulantes.stream()
                        .mapToLong(pe -> "Desaprobado".equals(pe.getEstado()) ? 1 : 0).sum();
                long noSePresentaron = listaPostulantes.stream()
                        .mapToLong(pe -> "No se presentó".equals(pe.getEstado()) ? 1 : 0).sum();
                long pendientes = listaPostulantes.stream()
                        .mapToLong(pe -> "Pendiente".equals(pe.getEstado()) ||
                                "Evaluando".equals(pe.getEstado()) ? 1 : 0).sum();

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>("Aprobados", aprobados));
                series.getData().add(new XYChart.Data<>("Desaprobados", desaprobados));
                series.getData().add(new XYChart.Data<>("No se presentaron", noSePresentaron));
                series.getData().add(new XYChart.Data<>("Pendientes", pendientes));

                chartResultados.getData().clear();
                chartResultados.getData().add(series);
            });
        } catch (Exception ex) {
            System.err.println("Error actualizando chart: " + ex.getMessage());
        }
    }

    private void filtrarPostulantes() {
        // TODO: Implementar filtrado por escuela y proceso
        String escuelaSeleccionada = cmbEscuela.getValue();
        String procesoSeleccionado = cmbProceso.getValue();

        // Por ahora solo refrescar la vista
        tablePostulantes.refresh();
        actualizarChart();
    }

    @FXML
    private void limpiarDatos() {
        try {
            // Cancelar cualquier tarea en ejecución de forma segura
            if (tareaActual != null && tareaActual.isRunning()) {
                tareaActual.cancel(true);

                // Esperar un momento para que la tarea se cancele
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    ejecutarLimpieza();
                });
            } else {
                ejecutarLimpieza();
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al limpiar datos: " + ex.getMessage());
            // Intentar limpiar de todos modos
            ejecutarLimpieza();
        }
    }

    private void ejecutarLimpieza() {
        Platform.runLater(() -> {
            try {
                // Desvincular el progress bar de cualquier tarea
                progressBar.progressProperty().unbind();

                // Limpiar la lista de postulantes
                listaPostulantes.clear();

                // Reiniciar progress bar
                progressBar.setProgress(0);

                // Restaurar estado inicial
                lblProgreso.setText("Sistema listo - Gabarito generado");
                lblEstadisticas.setText("");

                // Limpiar chart de forma segura
                chartResultados.getData().clear();

                // Limpiar estadísticas detalladas
                lblPromedioGeneral.setText("--");
                lblMejorPuntaje.setText("--");
                lblTiempoPromedio.setText("--");
                lblTasaAprobacion.setText("--");

                // Deshabilitar botones de evaluación
                btnEvaluarSecuencial.setDisable(true);
                btnEvaluarParalelo.setDisable(true);

                // Habilitar botón generar
                btnGenerar.setDisable(false);

                // Resetear ComboBoxes
                cmbEscuela.setValue("Todas las Escuelas");
                cmbProceso.setValue("Todos los Procesos");

                // Regenerar gabarito
                gabaritoRespuestas = generarGabaritoAleatorio();

                // Refrescar tabla
                tablePostulantes.refresh();

                // Resetear spinner a valor por defecto
                spinnerCantidad.getValueFactory().setValue(50);

            } catch (Exception ex) {
                System.err.println("Error durante la limpieza: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    /**
     * Genera un gabarito aleatorio de 100 preguntas
     */
    private String generarGabaritoAleatorio() {
        StringBuilder gabarito = new StringBuilder();
        String opciones = "ABCD";
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 100; i++) {
            gabarito.append(opciones.charAt(random.nextInt(4)));
        }

        return gabarito.toString();
    }

    /**
     * Genera postulantes de prueba con datos realistas
     */
    private List<Postulante> generarPostulantesPrueba(int cantidad) {
        String[] nombres = {
                "Ana María", "Carlos Eduardo", "María José", "José Luis", "Luis Alberto",
                "Carmen Rosa", "Pedro Miguel", "Rosa Elena", "Miguel Ángel", "Elena Patricia",
                "Jorge Antonio", "Patricia Isabel", "Antonio Manuel", "Isabel Cristina",
                "Manuel Francisco", "Cristina Andrea", "Francisco Javier", "Andrea Lucía",
                "Roberto Carlos", "Sofía Alejandra", "Diego Fernando", "Valeria Nicole",
                "Sebastián Mateo", "Camila Valentina", "Alejandro Nicolás", "Isabella Fernanda"
        };

        String[] apellidos = {
                "García Pérez", "Rodríguez López", "López Martínez", "Martínez Sánchez",
                "Sánchez González", "Pérez Fernández", "González Ruiz", "Fernández Díaz",
                "Ruiz Moreno", "Díaz Muñoz", "Moreno Álvarez", "Muñoz Romero",
                "Álvarez Castro", "Romero Silva", "Castro Vargas", "Silva Herrera",
                "Vargas Jiménez", "Herrera Mendoza", "Jiménez Aguilar", "Mendoza Torres"
        };

        String[] escuelasNombres = {
                "Medicina Humana",
                "Ingeniería de Sistemas e Informática",
                "Derecho y Ciencia Política",
                "Administración",
                "Contabilidad",
                "Psicología",
                "Educación",
                "Medicina Veterinaria",
                "Odontología",
                "Farmacia y Bioquímica",
                "Enfermería",
                "Obstetricia"
        };

        String[] procesosNombres = {
                "2024-I", "2024-II", "2025-I"
        };

        String[] localesNombres = {
                "Ciudad Universitaria", "Sede Central", "Local San Fernando", "Local Villarreal"
        };

        java.util.Random random = new java.util.Random();

        return java.util.stream.IntStream.range(1, cantidad + 1)
                .mapToObj(i -> {
                    String nombre = nombres[random.nextInt(nombres.length)];
                    String apellido = apellidos[random.nextInt(apellidos.length)];
                    String codigoPostulante = String.format("POST%06d", i);

                    // Crear postulante
                    Postulante postulante = new Postulante(
                            codigoPostulante,
                            nombre + " " + apellido,
                            random.nextInt(escuelasNombres.length) + 1,
                            random.nextInt(procesosNombres.length) + 1
                    );

                    // Crear y asignar escuela profesional
                    EscuelaProfesional escuela = new EscuelaProfesional();
                    escuela.setId(postulante.getIdEscuela());
                    escuela.setNombre(escuelasNombres[postulante.getIdEscuela() - 1]);
                    escuela.setIdLocal(random.nextInt(localesNombres.length) + 1);

                    // Crear y asignar local
                    Local local = new Local();
                    local.setId(escuela.getIdLocal());
                    local.setNombre(localesNombres[escuela.getIdLocal() - 1]);
                    escuela.setLocal(local);

                    postulante.setEscuelaProfesional(escuela);

                    // Crear y asignar año proceso
                    AnioProceso proceso = new AnioProceso();
                    proceso.setId(postulante.getIdProceso());
                    proceso.setYear(2024 + (postulante.getIdProceso() - 1) / 2);
                    proceso.setProceso(procesosNombres[postulante.getIdProceso() - 1]);
                    postulante.setAnioProceso(proceso);

                    return postulante;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            } catch (Exception ex) {
                System.err.println("Error mostrando alerta: " + ex.getMessage());
            }
        });
    }

    public void shutdown() {
        try {
            if (tareaActual != null && tareaActual.isRunning()) {
                tareaActual.cancel(true);
            }
        } catch (Exception ex) {
            System.err.println("Error durante shutdown: " + ex.getMessage());
        }
    }
}