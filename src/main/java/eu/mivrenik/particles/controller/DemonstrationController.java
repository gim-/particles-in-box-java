package eu.mivrenik.particles.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.model.ExperimentState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

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

    private void setState(final int newValue, boolean setSlider) throws Exception {
        ExperimentState state = loader.getState(newValue);

        double timeElapsed = state.getTime() / 1_000_000.0;
        timeElapsedLabel.setText("Time elapsed: " + timeElapsedFormat.format(timeElapsed) + "s");

        redraw(state);

        if (setSlider) {
            timeSlider.setValue((double) newValue);
        }
    }

    private void redraw(final ExperimentState state) {
        // TODO: implement canvas redraw
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
