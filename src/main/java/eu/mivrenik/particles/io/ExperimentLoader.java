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
import eu.mivrenik.particles.model.Particle;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ExperimentLoader {
    public static final int SETTINGS_SIZE = 72; // refer to README#Technical Information for more detail
    private final int dataChunkSize;

    private final ExperimentSettings experimentSettings;
    private int stateCount;
    private RandomAccessFile dataSource;

    private ExperimentSettings readWorldSettings(final RandomAccessFile dataSource) throws IOException {
        int particleCountLeft = dataSource.readInt();
        int particleCountRight = dataSource.readInt();
        float initialSpeed = dataSource.readFloat();
        float speedLoss = dataSource.readFloat();
        float speedDeltaTop = dataSource.readFloat();
        float speedDeltaSides = dataSource.readFloat();
        float speedDeltaBottom = dataSource.readFloat();
        float g = dataSource.readFloat();
        float boxWidth = dataSource.readFloat();
        float boxHeight = dataSource.readFloat();
        float barrierPosX = dataSource.readFloat();
        float barrierWidth = dataSource.readFloat();
        float holePosY = dataSource.readFloat();
        float holeHeight = dataSource.readFloat();
        float particleRadius = dataSource.readFloat();
        int fps = dataSource.readInt();
        int duration = dataSource.readInt();
        int seed = dataSource.readInt();

        return ExperimentSettings.newBuilder()
                .particleCount(particleCountLeft, particleCountRight)
                .initialSpeed(initialSpeed)
                .boxSize(boxWidth, boxHeight)
                .barrier(barrierPosX, barrierWidth)
                .hole(holePosY, holeHeight)
                .speedDelta(speedDeltaTop, speedDeltaSides, speedDeltaBottom)
                .speedLoss(speedLoss)
                .particleRadius(particleRadius)
                .g(g)
                .fps(fps)
                .duration(duration)
                .seed(seed)
                .build();
    }


    public ExperimentLoader(final File sourceFile) throws IOException {
        this.dataSource = new RandomAccessFile(sourceFile, "r");

        experimentSettings = readWorldSettings(this.dataSource);

        // refer to README#Technical Information for more detail on calculating those numbers
        dataChunkSize = experimentSettings.getParticleCount() * 36 + 8;

        stateCount = (int) ((dataSource.length() - SETTINGS_SIZE) / dataChunkSize);
    }

    public int getDataChunkSize() {
        return dataChunkSize;
    }

    public int getStateCount() {
        return stateCount;
    }

    public ExperimentSettings getExperimentSettings() {
        return experimentSettings;
    }

    public RandomAccessFile getDataSource() {
        return dataSource;
    }

    public ExperimentState getState(final int index) throws Exception {
        if (index < 0 || index >= getStateCount()) {
            throw new IndexOutOfBoundsException();
        }

        dataSource.seek(getStatePosition(index));

        long time = dataSource.readLong();
        Particle[] particles = new Particle[experimentSettings.getParticleCount()];

        for (int i = 0; i < experimentSettings.getParticleCount(); i++) {
            particles[i] = Particle.newBuilder()
                    .id(dataSource.readInt())
                    .position(dataSource.readDouble(), dataSource.readDouble())
                    .velocity(dataSource.readDouble(), dataSource.readDouble())
                    .build();
        }

        return ExperimentState.newBuilder()
                .time(time)
                .particles(particles)
                .settings(experimentSettings)
                .build();
    }

    private long getStatePosition(final int index) {
        return SETTINGS_SIZE + index * dataChunkSize;
    }
}
