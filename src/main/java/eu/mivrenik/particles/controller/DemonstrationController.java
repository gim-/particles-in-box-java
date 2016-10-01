package eu.mivrenik.particles.controller;

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.model.ExperimentState;
import eu.mivrenik.particles.model.Particle;
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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Demonstration scene controller.
 */
public class DemonstrationController implements Initializable {
    private static final Logger LOG = Logger.getLogger(DemonstrationController.class.getName());

    private NumberFormat timeElapsedFormat = new DecimalFormat("#.##");

    private ExperimentLoader loader;

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
    private BarChart<Float, Float> barChart;
    @FXML
    private LineChart<Float, Float> lineChart;

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

    private void redrawMaxwellDistribution(final ExperimentState state, int binsNum) {
        double[] x = new double[binsNum];
        double[] y = new double[binsNum];

        double maxVelocity = Double.MIN_VALUE;
        double minVelocity = Double.MAX_VALUE;
        double heightMax = Double.MIN_VALUE;
        double heightMin = Double.MAX_VALUE;

        for (Particle particle : state.getParticles()) {
            double speed = particle.getSpeed();
            maxVelocity = Math.max(speed, maxVelocity);
            minVelocity = Math.min(speed, minVelocity);
        }

        double deltaVelocity = maxVelocity / binsNum;

        for (int i = 0; i < binsNum; i++) {
            x[i] = deltaVelocity * (i + 1);
            y[i] = 0;
        }

        for (Particle particle : state.getParticles()) {
            double speed = particle.getSpeed();
            int chunk = (int) ((speed - deltaVelocity / 2.0) / deltaVelocity);
            y[chunk]++;
        }

        for (int i = 0; i < binsNum; i++) {
            y[i] = y[i] / (double) state.getParticles().length;
        }

        for (double currHeight : y) {
            heightMin = Math.min(heightMin, currHeight);
            heightMax = Math.max(heightMax, currHeight);
        }

        XYChart.Series maxwellSeries = new XYChart.Series();
        for (int i = 0; i < binsNum; i++) {
            maxwellSeries.getData().add(new XYChart.Data(x[i], y[i]));
        }
        // TODO add bar chart for experimental [x, y]

        double probableVelocity = x[Arrays.asList(y).indexOf(heightMax)];
        double k = 4 / Math.sqrt(Math.PI) * Math.pow(1.0 / probableVelocity, 3.0);

        for (int i = 0; i < binsNum; i++) {
            y[i] = k * Math.pow(x[i], 2.0) * Math.exp(-Math.pow(x[i], 2.0) / Math.pow(probableVelocity, 2.0));
        }

        lineChart.getData().add(maxwellSeries);
    }

    private void redrawBoltzmannDistribution(final ExperimentState state, final int binsNum) {
        double heightMax = Double.MIN_VALUE;
        double heightMin = Double.MAX_VALUE;

        for (Particle particle : state.getParticles()) {
            heightMax = Math.max(heightMax, particle.getPosY());
            heightMin = Math.min(heightMin, particle.getPosY());
        }

        double deltaHeight = heightMax / binsNum;
        double[] x = new double[binsNum];
        int[] y = new int[binsNum];

        for (int i = 0; i < binsNum; i++) {
            y[i] = 0;
            x[i] = deltaHeight * (i + 1);
        }

        for (Particle particle : state.getParticles()) {
            int chunk = (int) ((particle.getPosY() - deltaHeight / 2.0) / deltaHeight);
            y[chunk]++;
        }

        XYChart.Series boltzmannSeries = new XYChart.Series();

        for (int i = 0; i < binsNum; i++) {
            boltzmannSeries.getData().add(new XYChart.Data(x[i], y[i]));
        }

        barChart.getData().add(boltzmannSeries);

        // TODO Set bar width
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
