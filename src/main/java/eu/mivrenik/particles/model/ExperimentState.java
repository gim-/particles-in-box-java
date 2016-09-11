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

import java.util.Arrays;

public final class ExperimentState {
    private final ExperimentSettings settings;
    private final Particle[] particles;
    private long time; // microseconds since the beginning of experiment

    private ExperimentState(final Builder builder) {
        settings = builder.settings;
        particles = builder.particles;
        time = builder.time;
    }

    public ExperimentSettings getSettings() {
        return settings;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentState that = (ExperimentState) o;

        if (time != that.time) return false;
        if (!settings.equals(that.settings)) return false;
        return Arrays.equals(particles, that.particles);

    }

    @Override
    public int hashCode() {
        int result = settings.hashCode();
        result = 31 * result + Arrays.hashCode(particles);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private ExperimentSettings settings;
        private Particle[] particles;
        private long time;

        public Builder particles(final Particle[] particles) {
            this.particles = particles;

            return this;
        }

        public Builder settings(final ExperimentSettings settings) {
            this.settings = settings;

            return this;
        }

        public Builder time(final long microseconds) {
            time = microseconds;

            return this;
        }

        public ExperimentState build() {
            return new ExperimentState(this);
        }
    }
}
