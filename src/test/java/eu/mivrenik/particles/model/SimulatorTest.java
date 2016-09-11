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
package eu.mivrenik.particles.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SimulatorTest {
    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void initialDistribution() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.particleCount(100, 100)
                .initialSpeed(1)
                .boxSize(500, 500)
                .speedDelta(1, 1, 1)
                .barrier(10, 2)
                .hole(10, 2)
                .fps(30)
                .duration(1)
                .seed(241)
                .speedLoss(0.01f)
                .g(9.8f)
                .particleRadius(1);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        ExperimentState startState = simulator.initialDistribution();
        assertEquals(0, startState.getTime());
        assertEquals(200, startState.getParticles().length);

        for (Particle particle: startState.getParticles()) {
            assertNotEquals(particle.getPosX(), Double.NaN);
            assertNotEquals(particle.getPosY(), Double.NaN);
            assertNotEquals(particle.getVelocityX(), Double.NaN);
            assertNotEquals(particle.getVelocityY(), Double.NaN);
        }
    }

    @Test
    public void initialDistributionWithoutParticles() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.particleCount(0, 0)
                .initialSpeed(1)
                .boxSize(500, 500)
                .speedDelta(1, 1, 1)
                .barrier(10, 2)
                .hole(10, 2)
                .fps(30)
                .duration(1)
                .seed(241)
                .speedLoss(0.01f)
                .g(9.8f)
                .particleRadius(1);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        ExperimentState startState = simulator.initialDistribution();
        assertEquals(0, startState.getTime());
        assertEquals(0, startState.getParticles().length);
    }

    @Test
    public void initialDistributionException() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.particleCount(500, 500)
                .initialSpeed(1)
                .boxSize(50, 50)
                .speedDelta(1, 1, 1)
                .barrier(10, 2)
                .hole(10, 2)
                .fps(30)
                .duration(1)
                .seed(241)
                .speedLoss(0.01f)
                .g(9.8f)
                .particleRadius(1);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        thrownException.expect(Exception.class);
        ExperimentState startState = simulator.initialDistribution();
    }

    @Test
    public void nextTimeStep() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.particleCount(100, 100)
                .initialSpeed(1)
                .boxSize(500, 500)
                .speedDelta(1, 1, 1)
                .barrier(10, 2)
                .hole(10, 2)
                .fps(30)
                .duration(1)
                .seed(241)
                .speedLoss(0.01f)
                .g(9.8f)
                .particleRadius(1);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        ExperimentState startState = simulator.initialDistribution();
        ExperimentState nextState = simulator.nextTimeStep(startState, 10);

        for (Particle particle: startState.getParticles()) {
            assertNotEquals(particle.getPosX(), Double.NaN);
            assertNotEquals(particle.getPosY(), Double.NaN);
            assertNotEquals(particle.getVelocityX(), Double.NaN);
            assertNotEquals(particle.getVelocityY(), Double.NaN);
        }
    }

    @Test
    public void nextTimeStepWithoutMoving() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.particleCount(100, 100)
                .initialSpeed(1)
                .boxSize(500, 500)
                .speedDelta(1, 1, 1)
                .barrier(10, 2)
                .hole(10, 2)
                .fps(30)
                .duration(1)
                .seed(241)
                .speedLoss(0.01f)
                .g(9.8f)
                .particleRadius(1);

        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        ExperimentState startState = simulator.initialDistribution();
        ExperimentState nextState = simulator.nextTimeStep(startState, 0);

        for (Particle particle: startState.getParticles()) {
            assertNotEquals(particle.getPosX(), Double.NaN);
            assertNotEquals(particle.getPosY(), Double.NaN);
            assertNotEquals(particle.getVelocityX(), Double.NaN);
            assertNotEquals(particle.getVelocityY(), Double.NaN);
        }

        Particle[] beforeMoving = startState.getParticles();
        Particle[] afterMoving = nextState.getParticles();

        Arrays.sort(beforeMoving,
                (p1, p2) -> { return p1.getId() - p2.getId(); });
        Arrays.sort(afterMoving,
                (p1, p2) -> { return p1.getId() - p2.getId(); });
        
        for (int i = 0; i < startState.getParticles().length; i++) {
            assertEquals(true, beforeMoving[i].equals(afterMoving[i]));
        }
    }
}
