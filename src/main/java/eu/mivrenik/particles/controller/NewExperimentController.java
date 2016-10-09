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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * New experiment scene controller.
 */
public class NewExperimentController {
    private static final Logger LOG = Logger.getLogger(NewExperimentController.class.getName());
    private File outputFile;
    private boolean fileSelected;
    @FXML
    private Parent rootLayout;
    @FXML
    private TextField outputFileTextField;
    @FXML
    private Button outputFileChoiceButton;
    @FXML
    private Label dropFileLabel;
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

        setDisableSpinners(true);
        dropFileLabel.setText("Selected: " + outputFile.getAbsolutePath()
                + "\nClick here again to reset");
        fileSelected = true;

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
    }

    private void setDisableSpinners(final boolean flag) {
        particleCountLeft.setDisable(flag);
        particleCountRight.setDisable(flag);
        particleRadius.setDisable(flag);
        initialSpeed.setDisable(flag);
        speedLoss.setDisable(flag);
        speedDeltaTop.setDisable(flag);
        speedDeltaBottom.setDisable(flag);
        speedDeltaSides.setDisable(flag);
        boxWidth.setDisable(flag);
        boxHeight.setDisable(flag);
        barrierPosX.setDisable(flag);
        barrierWidth.setDisable(flag);
        holePosY.setDisable(flag);
        holeHeight.setDisable(flag);
        g.setDisable(flag);
        duration.setDisable(flag);
        fps.setDisable(flag);
        outputFileTextField.setDisable(flag);
        outputFileChoiceButton.setDisable(flag);
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

        if (fileSelected) {
            // Show demonstration set
            Stage stage = new Stage();
            Scene scene = DemonstrationScene.newInstance(outputFile.getAbsolutePath());
            stage.setTitle("Demonstration");
            stage.setScene(scene);

            rootLayout.getScene().getWindow().hide();
            stage.show();
            return;
        }

        Stage progressBarStage = new Stage();
        Group group = new Group();
        VBox vBox = new VBox();
        HBox simulationTitleBox = new HBox();
        Label simulationTitle = new Label();
        HBox progressBarBox = new HBox();
        Button cancelButton = new Button();
        ProgressBar progressBar = new ProgressBar(0);
        HBox statusTextBox = new HBox();
        Label simulationStatus = new Label();
        Scene progressBarScene = new Scene(group, 170, 65);

        Task<Void> progressBarTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long simulationDuration = experimentSettings.getDuration() * 60 * 1000000;
                long remainingTime;
                long currTime = 0;

                while (currTime < simulationDuration) {
                    remainingTime = TimeUnit.MICROSECONDS.toSeconds(simulationDuration - currTime);
                    updateMessage(Long.toString(remainingTime) + " seconds remaining");
                    if (simulationWriter.getSimulator().getLastState() != null) {
                        currTime = simulationWriter.getSimulator().getLastState().getTime();
                        updateProgress(currTime, simulationDuration);
                    }
                }

                return null;
            }
        };

        progressBarTask.setOnSucceeded(e -> {
            progressBarStage.getScene().getWindow().hide();
            // TODO Pass data to a new stage
            Stage stage = new Stage();
            stage.setTitle("Demonstration");
            stage.setScene(DemonstrationScene.newInstance(outputFile.getAbsolutePath()));
            stage.show();
        });

        Task<Void> simulationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                simulationWriter.saveSimulation();
                return null;
            }
        };

        Thread simulationThread = new Thread(simulationTask);
        Thread progressBarThread = new Thread(progressBarTask);

        progressBarStage.setScene(progressBarScene);
        progressBarStage.setTitle("Simulation progress");

        simulationTitleBox.setSpacing(6);
        simulationTitle.setText("Simulation progress...");
        simulationTitleBox.getChildren().add(simulationTitle);

        progressBarBox.setSpacing(5);
        cancelButton.setText("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                simulationThread.stop();
                progressBarThread.stop();
                progressBarStage.close();
            }
        });
        progressBar.setMinHeight(28);
        progressBarBox.getChildren().addAll(progressBar, cancelButton);

        statusTextBox.setSpacing(3);
        statusTextBox.getChildren().add(simulationStatus);

        vBox.getChildren().addAll(simulationTitleBox, progressBarBox, statusTextBox);
        progressBarScene.setRoot(vBox);

        simulationStatus.textProperty().bind(progressBarTask.messageProperty());
        progressBar.progressProperty().bind(progressBarTask.progressProperty());

        progressBarStage.show();

        simulationThread.start();
        progressBarThread.setDaemon(true);
        progressBarThread.start();

        rootLayout.getScene().getWindow().hide();
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
     * Called when mouse clicked on area.
     */
    public final void onMouseClicked() throws IOException {
        if (!fileSelected) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file for opening");
            outputFile = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());

            if (outputFile == null) {
                LOG.info("File for opening is not selected");
            } else if (outputFile.getName().endsWith(".bin")) {
                openSimulationFile(outputFile);
            }
        } else {
            setDisableSpinners(false);
            fileSelected = false;
            dropFileLabel.setText("Drag and drop file here or click to select");
        }
    }

    /**
     * Called when drag entered the file drop area.
     */
    public final void onDragEntered() {
        dropFileLabel.setText("Drop it!");
    }

    /**
     * Called when drag exited from the file drop area.
     */
    public final void onDragExited() {
        if (!fileSelected)
            dropFileLabel.setText("Drag and drop file here or click to select");
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
