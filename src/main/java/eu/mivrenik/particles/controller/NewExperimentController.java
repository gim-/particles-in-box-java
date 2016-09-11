/*
 * Copyright (c) 2016 Andrejs Mivreņiks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.mivrenik.particles.controller;

import eu.mivrenik.particles.model.ExperimentSettings;
import eu.mivrenik.particles.io.SimulationWriter;
import eu.mivrenik.particles.model.Simulator;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * New experiment scene controller.
 */
public class NewExperimentController {
    private static final Logger LOG = Logger.getLogger(NewExperimentController.class.getName());
    private File outputFile = null;
    @FXML
    private Parent rootLayout;
    @FXML
    private TextField outputFileTextField;
    @FXML
    private Spinner particleCountLeft;
    @FXML
    private Spinner particleCountRight;
    @FXML
    private Spinner particleRadius;
    @FXML
    private Spinner initialSpeed;
    @FXML
    private Spinner speedLoss;
    @FXML
    private Spinner speedDeltaTop;
    @FXML
    private Spinner speedDeltaBottom;
    @FXML
    private Spinner speedDeltaSides;
    @FXML
    private Spinner boxWidth;
    @FXML
    private Spinner boxHeight;
    @FXML
    private Spinner barrierPosX;
    @FXML
    private Spinner barrierWidth;
    @FXML
    private Spinner holePosY;
    @FXML
    private Spinner holeHeight;
    @FXML
    private Spinner g;
    @FXML
    private Spinner duration;
    @FXML
    private Spinner fps;

    /**
     * Run experiment button click callback.
     */
    public final void onRunClicked() throws Exception {
        if (outputFile == null) {
            LOG.info("File is not selected");
        } else if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();

        int particleCountLeftVal = Integer.valueOf(particleCountLeft.getEditor().getText());
        int particleCountRightVal = Integer.valueOf(particleCountRight.getEditor().getText());
        float particleRadiusVal = Float.valueOf(particleRadius.getEditor().getText());
        float initialSpeedVal = Float.valueOf(initialSpeed.getEditor().getText());
        float speedLossVal = Float.valueOf(speedLoss.getEditor().getText());
        float speedDeltaTopVal = Float.valueOf(speedDeltaTop.getEditor().getText());
        float speedDeltaBottomVal = Float.valueOf(speedDeltaBottom.getEditor().getText());
        float speedDeltaSidesVal = Float.valueOf(speedDeltaSides.getEditor().getText());
        float boxWidthVal = Float.valueOf(boxWidth.getEditor().getText());
        float boxHeightVal = Float.valueOf(boxHeight.getEditor().getText());
        float barrierPosXVal = Float.valueOf(barrierPosX.getEditor().getText());
        float barrierWidthVal = Float.valueOf(barrierWidth.getEditor().getText());
        float holePosYVal = Float.valueOf(holePosY.getEditor().getText());
        float holeHeightVal = Float.valueOf(holeHeight.getEditor().getText());
        float gVal = Float.valueOf(g.getEditor().getText());
        int durationVal = Integer.valueOf(duration.getEditor().getText());
        int fpsVal = Integer.valueOf(fps.getEditor().getText());
        // TODO set seed value
        int seedVal = 255;

        LOG.info("Left " + particleCountLeftVal);

        builder.particleCount(particleCountLeftVal,
                                 particleCountRightVal);
        builder.initialSpeed(initialSpeedVal);
        builder.speedLoss(speedLossVal);
        builder.speedDelta(speedDeltaTopVal, speedDeltaSidesVal, speedDeltaBottomVal);
        builder.g(gVal);
        builder.boxSize(boxWidthVal, boxHeightVal);
        builder.barrier(barrierPosXVal, barrierWidthVal);
        builder.hole(holePosYVal, holeHeightVal);
        builder.particleRadius(particleRadiusVal);
        builder.fps(fpsVal);
        builder.duration(durationVal);
        builder.seed(seedVal);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        SimulationWriter simulationWriter = new SimulationWriter(simulator, outputFile);
        simulationWriter.saveSimulation();

        LOG.info("Run button clicked: " + Thread.currentThread());
    }

    /**
     * Choose output file button click callback.
     */
    public final void onChooseOutputFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose output file");
        fileChooser.setInitialFileName("particles-in-box-out.bin");
        outputFile = fileChooser.showSaveDialog(rootLayout.getScene().getWindow());
        if (outputFile != null) {
            LOG.info("Chosen output file: " + outputFile);
            outputFileTextField.setText(outputFile.getAbsolutePath());
        }
    }

    /**
     * Called when drag entered the file drop area.
     */
    public final void onDragEntered() {
        // TODO Show "drop" message
    }

    /**
     * Called when drag exited from the file drop area.
     */
    public final void onDragExited() {
        // TODO Restore default message
    }

    /**
     * Drag dropped to the file drop area.
     *
     * @param event
     *            Event data.
     */
    public final void onDragDropped(final DragEvent event) {
        boolean success = false;
        LOG.log(Level.FINE, "Drag dropped: " + event.getDragboard().getString());
        if (event.getDragboard().hasFiles()) {
            File file = event.getDragboard().getFiles().get(0);
            // Check if file has a valid extension
            if (file.getName().endsWith(".bin")) {
                // TODO Handle file
                LOG.info("Dropped file: " + file);
                success = true;
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Drag activity detected over the file drop area.
     *
     * @param event
     *            Event data.
     */
    public final void onDragOver(final DragEvent event) {
        event.acceptTransferModes(TransferMode.LINK);
        event.consume();
    }
}
