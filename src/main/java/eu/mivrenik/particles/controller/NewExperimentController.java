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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

/**
 * New experiment scene controller.
 */
public class NewExperimentController {
    private static final Logger LOG = Logger.getLogger(NewExperimentController.class.getName());

    @FXML
    private Parent rootLayout;
    @FXML
    private TextField outputFileTextField;

    /**
     * Run experiment button click callback.
     */
    public final void onRunClicked() {
        // TODO Do stuff
        LOG.info("Run button clicked: " + Thread.currentThread());
    }

    /**
     * Choose output file button click callback.
     */
    public final void onChooseOutputFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose output file");
        fileChooser.setInitialFileName("particles-in-box-out.bin");
        File outputFile = fileChooser.showSaveDialog(rootLayout.getScene().getWindow());
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
