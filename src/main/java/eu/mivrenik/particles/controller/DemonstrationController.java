package eu.mivrenik.particles.controller;

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.model.ExperimentSettings;
import eu.mivrenik.particles.model.ExperimentState;
import eu.mivrenik.particles.model.Particle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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

    private NumberFormat timeElapsedFormat = new DecimalFormat("0.00");

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
    @FXML
    private BarChart<String, Number> particlesNumBar;
    @FXML
    private LineChart<Double, Integer> leftPartLine;
    @FXML
    private LineChart<Double, Integer> rightPartLine;

    public DemonstrationController(final String filePath) throws IOException {
        File sourceFile = new File(filePath);
        loader = new ExperimentLoader(sourceFile);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            plotParticlesNumLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                this::onStateTimerLaunch));
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();

            timeline = null;
        }
    }

    private void onStateTimerLaunch(final ActionEvent evt) {
        if (currentState < loader.getStateCount() - 1 && playbackStarted) {
            int fps = (fpsInput.getEditor().getText().length() > 0)
                    ? Integer.valueOf(fpsInput.getEditor().getText())
                    : 1;

            long duration = (long) (1000 / fps);

            try {
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration),
                        this::onStateTimerLaunch));
                timeline.play();

                setState(currentState + 1, true);
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
        redrawParticlesNum(state);

        if (updateSlider) {
            timeSlider.setValue((double) newValue);
        }
    }

    private void initializeCanvas() {
        ExperimentSettings settings = loader.getExperimentSettings();

        // Adjust canvas size
        canvas.setWidth(canvas.getHeight() * settings.getBoxWidth() / settings.getBoxHeight());

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
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(0.02);
        gc.strokeRect(
                barrierXMin + 0.01,
                -0.01,
                barrierWidth,
                lowerBarrierHeight);
        gc.strokeRect(
                barrierXMin + 0.01,
                settings.getBoxHeight() - upperBarrierHeight + 0.01,
                barrierWidth,
                upperBarrierHeight);

        for (Particle p : state.getParticles()) {
            if ((p.getId() & 1) == 0) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.SKYBLUE);
            }

            gc.fillOval(p.getPosX(), p.getPosY(), particleR, particleR);
        }
    }

    private void plotParticlesNumLines() throws Exception {
        XYChart.Series leftPartSeries = new XYChart.Series<>();
        XYChart.Series rightPartSeries = new XYChart.Series<>();

        leftPartSeries.setName("Left particles");
        rightPartSeries.setName("Right particles");

        double leftBound = loader.getExperimentSettings().getBarrierPosX()
                - 0.5 * loader.getExperimentSettings().getBarrierWidth();
        double rightBound = loader.getExperimentSettings().getBarrierPosX()
                + 0.5 * loader.getExperimentSettings().getBarrierWidth();

        XYChart.Series<Double, Integer> leftPartFirstTypeSeries = new XYChart.Series<Double, Integer>();
        XYChart.Series<Double, Integer> leftPartSecondTypeSeries = new XYChart.Series<Double, Integer>();
        XYChart.Series<Double, Integer> rightPartFirstTypeSeries = new XYChart.Series<Double, Integer>();
        XYChart.Series<Double, Integer> rightPartSecondTypeSeries = new XYChart.Series<Double, Integer>();

        leftPartFirstTypeSeries.setName("Left particles");
        leftPartSecondTypeSeries.setName("Right particles");
        rightPartFirstTypeSeries.setName("Left particles");
        rightPartSecondTypeSeries.setName("Right particles");

        for (int i = 0; i < loader.getStateCount(); i += Math.floorDiv(loader.getStateCount(), 10)) {
            ExperimentState state = loader.getState(i);
            int leftFirstTypeNum = 0;
            int leftSecondTypeNum = 0;
            int rightFirstTypeNum = 0;
            int rightSecondTypeNum = 0;

            for (Particle particle : state.getParticles()) {
                if (particle.getPosX() < leftBound) {
                    if ((particle.getId() & 1) == 0) {
                        leftFirstTypeNum++;
                    } else {
                        leftSecondTypeNum++;
                    }
                } else if (particle.getPosX() > rightBound) {
                    if ((particle.getId() & 1) == 0) {
                        rightFirstTypeNum++;
                    } else {
                        rightSecondTypeNum++;
                    }
                }
            }

            double currTimeSec = state.getTime() / 1000000.0;
            leftPartFirstTypeSeries.getData().add(new XYChart.Data<Double, Integer>(currTimeSec,
                    leftFirstTypeNum));
            leftPartSecondTypeSeries.getData().add(new XYChart.Data<Double, Integer>(currTimeSec,
                    leftSecondTypeNum));
            rightPartFirstTypeSeries.getData().add(new XYChart.Data<Double, Integer>(currTimeSec,
                    rightFirstTypeNum));
            rightPartSecondTypeSeries.getData().add(new XYChart.Data<Double, Integer>(currTimeSec,
                    rightSecondTypeNum));
        }

        leftPartLine.getData().addAll(leftPartFirstTypeSeries, leftPartSecondTypeSeries);
        rightPartLine.getData().addAll(rightPartFirstTypeSeries, rightPartSecondTypeSeries);
    }

    private void redrawParticlesNum(final ExperimentState state) {
        // first type of particles - particles which originally located on the left side
        // second type of particles - particles which originally located on the right side
        int leftFirstTypeNum = 0;
        int leftSecondTypeNum = 0;
        int rightFirstTypeNum = 0;
        int rightSecondTypeNum = 0;

        XYChart.Series leftPartSeries = new XYChart.Series<>();
        XYChart.Series rightPartSeries = new XYChart.Series<>();

        leftPartSeries.setName("Left particles");
        rightPartSeries.setName("Right particles");

        double leftBound = state.getSettings().getBarrierPosX() - 0.5 * state.getSettings().getBarrierWidth();
        double rightBound = state.getSettings().getBarrierPosX() + 0.5 * state.getSettings().getBarrierWidth();

        for (Particle particle : state.getParticles()) {
            if (particle.getPosX() < leftBound) {
                if ((particle.getId() & 1) == 0) {
                    leftFirstTypeNum++;
                } else {
                    leftSecondTypeNum++;
                }
            } else if (particle.getPosX() > rightBound) {
                if ((particle.getId() & 1) == 0) {
                    rightFirstTypeNum++;
                } else {
                    rightSecondTypeNum++;
                }
            }
        }

        leftPartSeries.getData().add(new XYChart.Data("Left part", leftFirstTypeNum));
        leftPartSeries.getData().add(new XYChart.Data("Right part", rightFirstTypeNum));

        rightPartSeries.getData().add(new XYChart.Data("Left part", leftSecondTypeNum));
        rightPartSeries.getData().add(new XYChart.Data("Right part", rightSecondTypeNum));

        particlesNumBar.getData().clear();
        particlesNumBar.getData().addAll(leftPartSeries, rightPartSeries);
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

        experimentalSeries.setName("experimental");
        theoreticalSeries.setName("theoretical");

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
    public void onTimeSliderValueChanged(final Number oldValue, final Number newValue) {
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
