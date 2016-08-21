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

import java.util.Set;

public class ExperimentState {
    private final ExperimentSettings settings;
    private final Set<Particle> particles;
    private long time; // microseconds since the beginning of experiment

    ExperimentState(final Builder builder) {
        settings = builder.getSettings();
        particles = builder.getParticles();
        time = builder.getTime();
    }

    public ExperimentSettings getSettings() {
        return settings;
    }

    public Set<Particle> getParticles() {
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
        return particles.equals(that.particles);

    }

    @Override
    public int hashCode() {
        int result = settings.hashCode();
        result = 31 * result + particles.hashCode();
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    Builder newBuilder() {
        return new Builder();
    }

    class Builder {
        private ExperimentSettings settings;
        private Set<Particle> particles;
        private long time;

        Builder setParticles(final Set<Particle> particles) {
            this.particles = particles;

            return this;
        }

        Builder setSettings(final ExperimentSettings settings) {
            this.settings = settings;

            return this;
        }

        Builder setTime(final long microseconds) {
            time = microseconds;

            return this;
        }

        public ExperimentSettings getSettings() {
            return settings;
        }

        public Set<Particle> getParticles() {
            return particles;
        }

        public long getTime() {
            return time;
        }

        ExperimentState build() {
            return new ExperimentState(this);
        }
    }
}
