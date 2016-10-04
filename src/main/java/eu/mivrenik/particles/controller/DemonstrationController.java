package eu.mivrenik.particles.controller;

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.model.ExperimentState;
import eu.mivrenik.particles.model.Particle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

/**
 * Demonstration scene controller.
 */
public class DemonstrationController implements Initializable {
    private static final Logger LOG = Logger.getLogger(DemonstrationController.class.getName());

    private NumberFormat timeElapsedFormat = new DecimalFormat("#.##");

    private ExperimentLoader loader;

    private ObservableList<String> heightChunks = FXCollections.observableArrayList();

    @FXML
    private Canvas canvas;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeElapsedLabel;
    @FXML
    private Button playbackButton;
    @FXML
    private Spinner<Integer> fpsInput;
    @FXML
    private BarChart<String, Number> boltzmannChart;
    @FXML
    private LineChart<Double, Double> lineChart;

    public DemonstrationController(final String filePath) throws IOException {
        File sourceFile = new File(filePath);
        loader = new ExperimentLoader(sourceFile);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        timeSlider.setMax((double) loader.getStateCount() - 1);
        timeSlider.setMinorTickCount(1);

        // Time slider value changed listener
        timeSlider.valueProperty().addListener(
                (ov, o, n) -> onTimeSliderValueChanged(o, n));
        // FPS input value changed listener
        fpsInput.valueProperty().addListener(
                (ov, o, n) -> onFpsValueChanged(o, n));
    }

    private void setState(final int newValue, final boolean updateSlider) throws Exception {
        ExperimentState state = loader.getState(newValue);

        double timeElapsed = state.getTime() / 1_000_000.0;
        timeElapsedLabel.setText("Time elapsed: " + timeElapsedFormat.format(timeElapsed) + "s");

        redraw(state);
        redrawMaxwellDistribution(state, 20);
        redrawBoltzmannDistribution(state, 20);

        if (updateSlider) {
            timeSlider.setValue((double) newValue);
        }
    }

    private void redraw(final ExperimentState state) {
        // TODO Method stub.
    }

    private void redrawMaxwellDistribution(final ExperimentState state, final int binsNum) {
        ArrayList<Double> x = new ArrayList<>(binsNum);
        ArrayList<Double> yExperimental = new ArrayList<>(binsNum);

        DoubleStream speedStream = Arrays.stream(state.getParticles())
                .mapToDouble(Particle::getSpeed);
        double maxVelocity = speedStream.max().orElse(1.0);

        double deltaVelocity = maxVelocity / binsNum;

        XYChart.Series<Double, Double> experimentalSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> theoreticalSeries = new XYChart.Series<>();

        lineChart.getData().clear();

        for (int i = 0; i < binsNum; i++) {
            x.add(deltaVelocity * (i + 1));
            yExperimental.add(0.0);
        }

        for (Particle particle : state.getParticles()) {
            double speed = particle.getSpeed();
            int chunk = (int) ((speed - deltaVelocity / 2.0) / deltaVelocity);
            yExperimental.set(chunk, yExperimental.get(chunk) + 1);
        }

        for (int i = 0; i < binsNum; i++) {
            yExperimental.set(i, yExperimental.get(i) / (double) state.getParticles().length);
        }

        for (int i = 0; i < binsNum; i++) {
            experimentalSeries.getData().add(new XYChart.Data<>(x.get(i), yExperimental.get(i)));
        }

        double probableVelocity = x.get(yExperimental.indexOf(Collections.max(yExperimental)));
        double k = 4 / Math.sqrt(Math.PI) * Math.pow(1.0 / probableVelocity, 3.0);

        for (int i = 0; i < binsNum; i++) {
            double y = k * Math.pow(x.get(i), 2.0)
                    * Math.exp(-Math.pow(x.get(i), 2.0) / Math.pow(probableVelocity, 2.0));

            theoreticalSeries.getData().add(new XYChart.Data<>(x.get(i), y));
        }

        lineChart.getData().addAll(experimentalSeries, theoreticalSeries);
    }

    private void redrawBoltzmannDistribution(final ExperimentState state, final int binsNum) {
        final NumberFormat dataFormat = new DecimalFormat("#.##");

        final double deltaHeight = state.getSettings().getBoxHeight() / binsNum;
        double[] x = new double[binsNum];
        int[] y = new int[binsNum];
        XYChart.Series<String, Number> boltzmannSeries = new XYChart.Series<>();


        boltzmannChart.getData().clear();

        for (int i = 0; i < binsNum; i++) {
            y[i] = 0;
            x[i] = deltaHeight * (i + 1);
        }

        for (Particle particle : state.getParticles()) {
            int chunk = (int) ((particle.getPosY() - deltaHeight / 2.0) / deltaHeight);
            y[chunk]++;
        }

        for (int i = 0; i < binsNum; i++) {
            boltzmannSeries.getData().add(new XYChart.Data<>(dataFormat.format(x[i]),
                                                            y[i]));
        }

        boltzmannChart.getData().add(boltzmannSeries);
        boltzmannChart.setBarGap(0.0);
        boltzmannChart.setCategoryGap(0.0);
    }


    /**
     * Playback button click callback.
     */
    public void onPlaybackClicked() {
        // TODO Method stub.
        LOG.info("Playback clicked");
    }

    /**
     * Time slider valued changed callback. Registered in
     * {@link #initialize(URL, ResourceBundle)} method.
     *
     * @param oldValue
     *            Value changed from.
     * @param newValue
     *            Value changed to.
     */
    public void onTimeSliderValueChanged(final Number oldValue, final Number newValue)  {
        try {
            setState(newValue.intValue(), false);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * FPS input value changed callback. Registered in
     * {@link #initialize(URL, ResourceBundle)} method.
     *
     * @param oldValue
     *            Value changed from.
     * @param newValue
     *            Value changed to.
     */
    public void onFpsValueChanged(final int oldValue, final int newValue) {
        // TODO Method stub.
        LOG.info("FPS value changed from " + oldValue + " to " + newValue);
    }
}
