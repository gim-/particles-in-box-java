package eu.mivrenik.particles.controller;

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.model.ExperimentSettings;
import eu.mivrenik.particles.model.ExperimentState;
import eu.mivrenik.particles.model.Particle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

/**
 * Demonstration scene controller.
 */
public class DemonstrationController implements Initializable {
    private static final Logger LOG = Logger.getLogger(DemonstrationController.class.getName());

    private int currentState;
    private boolean playbackStarted;

    private NumberFormat timeElapsedFormat = new DecimalFormat("#.##");

    private ExperimentLoader loader;

    private Timeline timeline;

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
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        boltzmannChart.setLegendVisible(false);

        timeSlider.setMax((double) loader.getStateCount() - 1);
        timeSlider.setMinorTickCount(1);

        // Time slider value changed listener
        timeSlider.valueProperty().addListener(
                (ov, o, n) -> onTimeSliderValueChanged(o, n));
        // FPS input value changed listener
        fpsInput.valueProperty().addListener(
                (ov, o, n) -> onFpsValueChanged(o, n));

        initializeCanvas();
        try {
            setState(0, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startPlayback();
    }

    private void startPlayback() {
        stopTimer();

        playbackStarted = true;

        startTimer();

        playbackButton.setText("▯▯");
    }

    private void stopPlayback() {
        stopTimer();

        playbackStarted = false;

        playbackButton.setText("▷");
    }

    private void startTimer() {
        long duration = (long) (1000 / fpsInput.getValue());

        timeline = new Timeline(new KeyFrame(Duration.millis(duration),
                (e) -> this.onStateTimerLaunch()));
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();

            timeline = null;
        }
    }

    private void onStateTimerLaunch() {
        if (currentState < loader.getStateCount() - 1 && playbackStarted) {
            int fps = (fpsInput.getEditor().getText().length() > 0)
                    ? Integer.valueOf(fpsInput.getEditor().getText())
                    : 1;

            long duration = (long) (1000 / fps);

            try {
                setState(currentState + 1, true);

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration),
                        (e) -> this.onStateTimerLaunch()));
                timeline.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            stopPlayback();
        }
    }

    private void setState(final int newValue, final boolean updateSlider) throws Exception {
        if (newValue >= loader.getStateCount()) {
            // Do something about it.
            // An exception, perhaps?
            return;
        }

        ExperimentState state = loader.getState(newValue);

        double timeElapsed = state.getTime() / 1_000_000.0;

        currentState = newValue;

        timeElapsedLabel.setText("Time elapsed: " + timeElapsedFormat.format(timeElapsed) + "s");

        redraw(state);
        redrawMaxwellDistribution(state, 20);
        redrawBoltzmannDistribution(state, 17);

        if (updateSlider) {
            timeSlider.setValue((double) newValue);
        }
    }

    private void initializeCanvas() {
        ExperimentSettings settings = loader.getExperimentSettings();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mxx = canvas.getWidth() / settings.getBoxWidth();
        double myy = -canvas.getHeight() / settings.getBoxHeight();

        gc.setTransform(mxx, 0.0, 0.0, myy, 0.0, canvas.getHeight());
    }

    private void redraw(final ExperimentState state) {
        ExperimentSettings settings = loader.getExperimentSettings();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double barrierWidth = settings.getBarrierWidth();
        double barrierXMin = settings.getBarrierPosX() - barrierWidth / 2;
        double holeHeight = settings.getHoleHeight();
        double upperBarrierHeight = settings.getBoxHeight() - settings.getHolePosY() - holeHeight / 2;
        double lowerBarrierHeight = settings.getHolePosY() - holeHeight / 2;
        double particleR = settings.getParticleRadius();

        gc.clearRect(0, 0, settings.getBoxWidth(), settings.getBoxHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(barrierXMin, 0, barrierWidth, lowerBarrierHeight);
        gc.fillRect(barrierXMin, settings.getBoxHeight() - upperBarrierHeight, barrierWidth, upperBarrierHeight);

        for (Particle p : state.getParticles()) {
            if ((p.getId() & 1) == 0) {
                gc.setFill(Color.INDIANRED);
            } else {
                gc.setFill(Color.BLUEVIOLET);
            }

            gc.fillOval(p.getPosX(), p.getPosY(), particleR, particleR);
        }
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

        for (int i = -1; i < binsNum; i++) {
            x.add(deltaVelocity * (i + 1));
            yExperimental.add(0.0);
        }

        for (Particle particle : state.getParticles()) {
            double speed = particle.getSpeed();

            int idx = Math.min((int) Math.floor(speed / deltaVelocity), binsNum - 1);
            yExperimental.set(idx, yExperimental.get(idx) + 1);
        }

        for (int i = 0; i < yExperimental.size(); i++) {
            yExperimental.set(i, yExperimental.get(i) / (state.getParticles().length * deltaVelocity));
        }

        for (int i = 0; i < yExperimental.size(); i++) {
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
        if (playbackStarted) {
            stopPlayback();
        } else {
            startPlayback();
        }
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
