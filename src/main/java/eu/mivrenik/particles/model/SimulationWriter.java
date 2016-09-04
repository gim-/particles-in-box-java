package eu.mivrenik.particles.model;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimulationWriter {
    private Simulator simulator;
    private String filename;

    public SimulationWriter(final Simulator simulator, final String filename) {
        this.simulator = simulator;
        this.filename = filename;
    }

    public void saveSimulation() throws Exception {
        ExperimentState currState = simulator.initialDistribution();
        ExperimentSettings experimentSettings = simulator.getSettings();
        long duration = TimeUnit.MINUTES.toMicros(experimentSettings.getDuration());
        long deltaTime = Math.floorDiv(duration, experimentSettings.getFps());
        long currSnap = 0;

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));

            out.writeInt(experimentSettings.getParticleCountLeft());
            out.writeInt(experimentSettings.getParticleCountRight());
            out.writeFloat(experimentSettings.getInitialSpeed());
            out.writeFloat(experimentSettings.getSpeedLoss());
            out.writeFloat(experimentSettings.getSpeedDeltaTop());
            out.writeFloat(experimentSettings.getSpeedDeltaSides());
            out.writeFloat(experimentSettings.getSpeedDeltaBottom());
            out.writeFloat(experimentSettings.getG());
            out.writeFloat(experimentSettings.getBoxWidth());
            out.writeFloat(experimentSettings.getBoxHeight());
            out.writeFloat(experimentSettings.getBarrierPosX());
            out.writeFloat(experimentSettings.getBarrierWidth());
            out.writeFloat(experimentSettings.getHolePosY());
            out.writeFloat(experimentSettings.getHoleHeight());
            out.writeFloat(experimentSettings.getParticleRadius());
            out.writeInt(experimentSettings.getFps());
            out.writeInt(experimentSettings.getDuration());
            out.writeInt(experimentSettings.getSeed());

            while (currState.getTime() < duration) {
                if (currState.getTime() > currSnap * deltaTime && currState.getTime() < (currSnap + 1) * deltaTime) {
                    out.writeDouble(currState.getTime());
                    for (Particle particle : currState.getParticles()) {
                        out.writeInt(particle.getId());
                        out.writeDouble(particle.getPosX());
                        out.writeDouble(particle.getPosY());
                        out.writeDouble(particle.getVelocityX());
                        out.writeDouble(particle.getVelocityY());
                    }
                    currSnap++;
                }

                currState = simulator.nextTimeStep(currState, Simulator.calculateTimeStep(currState));
            }

            out.flush();
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
