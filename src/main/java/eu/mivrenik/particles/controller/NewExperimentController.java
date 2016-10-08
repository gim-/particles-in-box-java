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

import eu.mivrenik.particles.io.ExperimentLoader;
import eu.mivrenik.particles.io.SimulationWriter;
import eu.mivrenik.particles.model.ExperimentSettings;
import eu.mivrenik.particles.model.Simulator;
import eu.mivrenik.particles.scene.DemonstrationScene;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * New experiment scene controller.
 */
public class NewExperimentController {
    private static final Logger LOG = Logger.getLogger(NewExperimentController.class.getName());
    private File outputFile = null;
    private boolean isLoaded = false;
    @FXML
    private Parent rootLayout;
    @FXML
    private TextField outputFileTextField;
    @FXML
    private Pane dropFileSpace;
    @FXML
    private Spinner<Integer> particleCountLeft;
    @FXML
    private Spinner<Integer> particleCountRight;
    @FXML
    private Spinner<Float> particleRadius;
    @FXML
    private Spinner<Float> initialSpeed;
    @FXML
    private Spinner<Float> speedLoss;
    @FXML
    private Spinner<Float> speedDeltaTop;
    @FXML
    private Spinner<Float> speedDeltaBottom;
    @FXML
    private Spinner<Float> speedDeltaSides;
    @FXML
    private Spinner<Float> boxWidth;
    @FXML
    private Spinner<Float> boxHeight;
    @FXML
    private Spinner<Float> barrierPosX;
    @FXML
    private Spinner<Float> barrierWidth;
    @FXML
    private Spinner<Float> holePosY;
    @FXML
    private Spinner<Float> holeHeight;
    @FXML
    private Spinner<Float> g;
    @FXML
    private Spinner<Integer> duration;
    @FXML
    private Spinner<Integer> fps;

    public void openSimulationFile(final File file) throws IOException {
        outputFile = file;
        ExperimentLoader loader = new ExperimentLoader(file);
        ExperimentSettings experimentSettings = loader.getExperimentSettings();

        int particleCountLeftVal = experimentSettings.getParticleCountLeft();
        int particleCountRightVal = experimentSettings.getParticleCountRight();
        float particleRadiusVal = experimentSettings.getParticleRadius();
        float initialSpeedVal = experimentSettings.getInitialSpeed();
        float speedLossVal = experimentSettings.getSpeedLoss();
        float speedDeltaTopVal = experimentSettings.getSpeedDeltaTop();
        float speedDeltaBottomVal = experimentSettings.getSpeedDeltaBottom();
        float speedDeltaSidesVal = experimentSettings.getSpeedDeltaSides();
        float boxWidthVal = experimentSettings.getBoxWidth();
        float boxHeightVal = experimentSettings.getBoxHeight();
        float barrierPosXVal = experimentSettings.getBarrierPosX();
        float barrierWidthVal = experimentSettings.getBarrierWidth();
        float holePosYVal = experimentSettings.getHolePosY();
        float holeHeightVal = experimentSettings.getHoleHeight();
        float gVal = experimentSettings.getG();
        int durationVal = experimentSettings.getDuration();
        int fpsVal = experimentSettings.getFps();

        particleCountLeft.getEditor().setText(Integer.toString(particleCountLeftVal));
        particleCountRight.getEditor().setText(Integer.toString(particleCountRightVal));
        particleRadius.getEditor().setText(Float.toString(particleRadiusVal));
        initialSpeed.getEditor().setText(Float.toString(initialSpeedVal));
        speedLoss.getEditor().setText(Float.toString(speedLossVal));
        speedDeltaTop.getEditor().setText(Float.toString(speedDeltaTopVal));
        speedDeltaBottom.getEditor().setText(Float.toString(speedDeltaBottomVal));
        speedDeltaSides.getEditor().setText(Float.toString(speedDeltaSidesVal));
        boxWidth.getEditor().setText(Float.toString(boxWidthVal));
        boxHeight.getEditor().setText(Float.toString(boxHeightVal));
        barrierPosX.getEditor().setText(Float.toString(barrierPosXVal));
        barrierWidth.getEditor().setText(Float.toString(barrierWidthVal));
        holePosY.getEditor().setText(Float.toString(holePosYVal));
        holeHeight.getEditor().setText(Float.toString(holeHeightVal));
        g.getEditor().setText(Float.toString(gVal));
        duration.getEditor().setText(Integer.toString(durationVal));
        fps.getEditor().setText(Integer.toString(fpsVal));

        particleCountLeft.setEditable(false);
        particleCountRight.setEditable(false);
        particleRadius.setEditable(false);
        initialSpeed.setEditable(false);
        speedLoss.setEditable(false);
        speedDeltaTop.setEditable(false);
        speedDeltaBottom.setEditable(false);
        speedDeltaSides.setEditable(false);
        boxWidth.setEditable(false);
        boxHeight.setEditable(false);
        barrierPosX.setEditable(false);
        barrierWidth.setEditable(false);
        holePosY.setEditable(false);
        holeHeight.setEditable(false);
        g.setEditable(false);
        duration.setEditable(false);
        fps.setEditable(false);

        Label fileLabel = new Label(outputFile.getAbsolutePath());
        fileLabel.setLayoutX(23);
        fileLabel.setLayoutY(12);
        dropFileSpace.getChildren().add(fileLabel);

        isLoaded = true;
    }

    /**
     * Run experiment button click callback.
     */
    public final void onRunClicked() throws Exception {
        if (outputFile == null) {
            outputFile = new File(outputFileTextField.getText());
        }

        if (!outputFile.exists()) {
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

        if (!isLoaded) {
            simulationWriter.saveSimulation();
        }

        // Show demonstration set
        Stage stage = new Stage();
        Scene scene = DemonstrationScene.newInstance(outputFile.getAbsolutePath());
        stage.setTitle("Demonstration");
        stage.setScene(scene);

        rootLayout.getScene().getWindow().hide();
        stage.show();
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

        dropFileSpace.getChildren().clear();
        isLoaded = false;

        particleCountLeft.setEditable(true);
        particleCountRight.setEditable(true);
        particleRadius.setEditable(true);
        initialSpeed.setEditable(true);
        speedLoss.setEditable(true);
        speedDeltaTop.setEditable(true);
        speedDeltaBottom.setEditable(true);
        speedDeltaSides.setEditable(true);
        boxWidth.setEditable(true);
        boxHeight.setEditable(true);
        barrierPosX.setEditable(true);
        barrierWidth.setEditable(true);
        holePosY.setEditable(true);
        holeHeight.setEditable(true);
        g.setEditable(true);
        duration.setEditable(true);
        fps.setEditable(true);
    }

    /**
     * Called when mouse clicked on area.
     */
    public final void onMouseClicked() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file for opening");
        outputFile = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());

        if (outputFile.getName().endsWith(".bin")) {
            openSimulationFile(outputFile);
            isLoaded = true;
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
    public final void onDragDropped(final DragEvent event) throws IOException {
        boolean success = false;
        LOG.log(Level.FINE, "Drag dropped: " + event.getDragboard().getString());
        if (event.getDragboard().hasFiles()) {
            File file = event.getDragboard().getFiles().get(0);
            // Check if file has a valid extension
            if (file.getName().endsWith(".bin")) {
                openSimulationFile(file);
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
