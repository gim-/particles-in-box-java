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
import eu.mivrenik.particles.model.SimulationWriter;
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

        int particleCountLeftVal = (Integer) particleCountLeft.getValue();
        int particleCountRightVal = (Integer) particleCountRight.getValue();
        float particleRadiusVal = ((Double) particleRadius.getValue()).floatValue();
        float initialSpeedVal = ((Double) initialSpeed.getValue()).floatValue();
        float speedLossVal = ((Double) speedLoss.getValue()).floatValue();
        float speedDeltaTopVal = ((Double) speedDeltaTop.getValue()).floatValue();
        float speedDeltaBottomVal = ((Double) speedDeltaBottom.getValue()).floatValue();
        float speedDeltaSidesVal = ((Double) speedDeltaSides.getValue()).floatValue();
        float boxWidthVal = ((Double) boxWidth.getValue()).floatValue();
        float boxHeightVal = ((Double) boxHeight.getValue()).floatValue();
        float barrierPosXVal = ((Double) barrierPosX.getValue()).floatValue();
        float barrierWidthVal = ((Double) barrierWidth.getValue()).floatValue();
        float holePosYVal = ((Double) holePosY.getValue()).floatValue();
        float holeHeightVal = ((Double) holeHeight.getValue()).floatValue();
        float gVal = ((Double) g.getValue()).floatValue();
        int durationVal = (Integer) duration.getValue();
        int fpsVal = (Integer) fps.getValue();
        // TODO set seed value
        int seedVal = 255;

        builder.setParticleCount(particleCountLeftVal,
                                 particleCountRightVal);
        builder.setInitialSpeed(initialSpeedVal);
        builder.setSpeedLoss(speedLossVal);
        builder.setSpeedDelta(speedDeltaTopVal, speedDeltaSidesVal, speedDeltaBottomVal);
        builder.setG(gVal);
        builder.setBoxSize(boxWidthVal, boxHeightVal);
        builder.setBarrier(barrierPosXVal, barrierWidthVal);
        builder.setHole(holePosYVal, holeHeightVal);
        builder.setParticleRadius(particleRadiusVal);
        builder.setFps(fpsVal);
        builder.setLength(durationVal);
        builder.setSeed(seedVal);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        SimulationWriter simulationWriter = new SimulationWriter(simulator, outputFile.getName());
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
