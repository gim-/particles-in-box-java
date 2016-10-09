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
package eu.mivrenik.particles;

import java.util.Locale;
import java.util.logging.Logger;

import eu.mivrenik.particles.scene.NewExperimentScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class that is used as entry point.
 */
public class AppEntryPoint extends Application {
    private static final Logger LOG = Logger.getLogger(AppEntryPoint.class.getName());

    /**
     * Application entry point.
     *
     * @param args
     *            Launch arguments
     */
    public static void main(final String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }

    /**
     * Initialise main view stage.
     */
    @Override
    public final void start(final Stage stage) throws Exception {
        LOG.info("Starting particles in box application");
        Scene scene = NewExperimentScene.newInstance();
        stage.setTitle("New experiment");
        stage.setScene(scene);
        stage.show();
        // Restrict resizing
        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
    }
}
