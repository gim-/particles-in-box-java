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
package eu.mivrenik.particles.io;

import eu.mivrenik.particles.model.ExperimentSettings;
import eu.mivrenik.particles.model.ExperimentState;
import eu.mivrenik.particles.model.Simulator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExperimentIOTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public static String FILE_NAME = "simulation_test.bin";

    private File file;
    private ExperimentSettings settings;
    private ExperimentLoader loader;

    @Before
    public void setUp() throws Exception {
        file = tmpFolder.newFile(FILE_NAME);

        settings = ExperimentSettings.newBuilder()
                .boxSize(100.0f, 100.0f)
                .barrier(45.0f, 10.0f)
                .hole(45.0f, 10.0f)
                .initialSpeed(1.0f)
                .speedDelta(0.05f, 0.05f, 0.05f)
                .speedLoss(0.005f)
                .particleRadius(0.05f)
                .particleCount(50, 50)
                .fps(30)
                .g(9.8f)
                .duration(1)
                .seed(5553535)
                .build();

        Simulator simulator = new Simulator(settings);
        SimulationWriter writer = new SimulationWriter(simulator, file);

        writer.saveSimulation();

        loader = new ExperimentLoader(file);
    }

    @Test
    public void testSizeCalculations() throws Exception {
        // refer to readme for explanation on those "magic numbers"

        assertEquals(3608, loader.getDataChunkSize());
        assertEquals(30 * 5 * 60, loader.getStateCount()); // fps * duration
    }

    @Test
    public void testInitialData() throws Exception {
        assertEquals(settings, loader.getExperimentSettings());

        ExperimentState state = loader.getState(0);

        assertEquals(0, state.getTime());
        assertEquals(settings, state.getSettings());
    }

    @Test
    public void testRandomAccess() throws Exception {
        ExperimentState state_base = loader.getState(53);
        ExperimentState state_forward = loader.getState(56);
        ExperimentState state_before = loader.getState(50);

        assertEquals(settings, state_base.getSettings());
        assertEquals(settings, state_forward.getSettings());
        assertEquals(settings, state_before.getSettings());

        assertTrue(state_base.getTime() < state_forward.getTime());
        assertTrue(state_before.getTime() < state_base.getTime());
    }


}
