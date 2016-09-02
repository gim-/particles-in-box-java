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
        builder.setParticleCount(100, 100)
                .setInitialSpeed(1)
                .setBoxSize(500, 500)
                .setSpeedDelta(1, 1, 1)
                .setBarrier(10, 2)
                .setHole(10, 2)
                .setFps(30)
                .setLength(1)
                .setSeed(241)
                .setSpeedLoss(0.01f)
                .setG(9.8f)
                .setParticleRadius(1);


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
        builder.setParticleCount(0, 0)
                .setInitialSpeed(1)
                .setBoxSize(500, 500)
                .setSpeedDelta(1, 1, 1)
                .setBarrier(10, 2)
                .setHole(10, 2)
                .setFps(30)
                .setLength(1)
                .setSeed(241)
                .setSpeedLoss(0.01f)
                .setG(9.8f)
                .setParticleRadius(1);


        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        ExperimentState startState = simulator.initialDistribution();
        assertEquals(0, startState.getTime());
        assertEquals(0, startState.getParticles().length);
    }

    @Test
    public void initialDistributionException() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.setParticleCount(500, 500)
                .setInitialSpeed(1)
                .setBoxSize(50, 50)
                .setSpeedDelta(1, 1, 1)
                .setBarrier(10, 2)
                .setHole(10, 2)
                .setFps(30)
                .setLength(1)
                .setSeed(241)
                .setSpeedLoss(0.01f)
                .setG(9.8f)
                .setParticleRadius(1);


        ExperimentSettings experimentSettings = builder.build();
        Simulator simulator = new Simulator(experimentSettings);
        thrownException.expect(Exception.class);
        ExperimentState startState = simulator.initialDistribution();
    }

    @Test
    public void nextTimeStep() throws Exception {
        ExperimentSettings.Builder builder = ExperimentSettings.newBuilder();
        builder.setParticleCount(100, 100)
                .setInitialSpeed(1)
                .setBoxSize(500, 500)
                .setSpeedDelta(1, 1, 1)
                .setBarrier(10, 2)
                .setHole(10, 2)
                .setFps(30)
                .setLength(1)
                .setSeed(241)
                .setSpeedLoss(0.01f)
                .setG(9.8f)
                .setParticleRadius(1);


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
        builder.setParticleCount(100, 100)
                .setInitialSpeed(1)
                .setBoxSize(500, 500)
                .setSpeedDelta(1, 1, 1)
                .setBarrier(10, 2)
                .setHole(10, 2)
                .setFps(30)
                .setLength(1)
                .setSeed(241)
                .setSpeedLoss(0.01f)
                .setG(9.8f)
                .setParticleRadius(1);


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
