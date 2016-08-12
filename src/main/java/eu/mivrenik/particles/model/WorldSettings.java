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

/**
 * Class primarily used to describe the conditions of an experiment in which
 * we observe the behavior of particles.
 *
 * One important part of the experiment is that all of those particles are
 * located within an enclosed area, which we call a box.
 *
 * There is one thing to keep in mind about the box, though: it has a barrier
 * that splits our box into two parts: the left and the right. However, to
 * let particles move freely across the box, there is a gap in the barrier,
 * which we call a hole.
 *
 * ____________________________________________________
 * |                   |         |                    |
 * |                   | barrier |                    |
 * |                   |         |                    |
 * |                   |_________|                    |
 * |                                                  |
 * |                      hole                        y <- holePosY
 * |                   ___________                    |
 * |                   | barrier |                    |
 * |                   |         |                    |
 * |                   |         |                    |
 * |___________________|____x____|____________________|
 *
 *                          ^ barrierPosX
 */

public final class WorldSettings {
    private final float boxWidth;
    private final float boxHeight;
    private final float speedDeltaTop;
    private final float speedDeltaSides;
    private final float speedDeltaBottom;
    private final float barrierPosX;
    private final float barrierWidth;
    private final float holePosY;
    private final float holeHeight;

    /**
     * Speed loss determines how much energy a particle
     * will lose after colliding with another particle.
     *
     * Resulting speed = original speed * (1 - speedLoss)
     */
    private final float speedLoss;
    private final float particleRadius;
    private final float g;

    private WorldSettings(final Builder builder) {
        boxWidth = builder.getBoxWidth();
        boxHeight = builder.getBoxHeight();
        speedDeltaTop = builder.getSpeedDeltaTop();
        speedDeltaSides = builder.getSpeedDeltaSides();
        speedDeltaBottom = builder.getSpeedDeltaBottom();
        barrierPosX = builder.getBarrierPosX();
        barrierWidth = builder.getBarrierWidth();
        holePosY = builder.getHolePosY();
        holeHeight = builder.getHoleHeight();
        speedLoss = builder.getSpeedLoss();
        particleRadius = builder.getParticleRadius();
        g = builder.getG();
    }

    public float getBoxWidth() {
        return boxWidth;
    }

    public float getBoxHeight() {
        return boxHeight;
    }

    public float getSpeedDeltaTop() {
        return speedDeltaTop;
    }

    public float getSpeedDeltaBottom() {
        return speedDeltaBottom;
    }

    public float getSpeedDeltaSides() {
        return speedDeltaSides;
    }

    public float getBarrierPosX() {
        return barrierPosX;
    }

    public float getBarrierWidth() {
        return barrierWidth;
    }

    public float getHolePosY() {
        return holePosY;
    }

    public float getHoleHeight() {
        return holeHeight;
    }

    public float getSpeedLoss() {
        return speedLoss;
    }

    public float getParticleRadius() {
        return particleRadius;
    }

    public float getG() {
        return g;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldSettings that = (WorldSettings) o;

        if (Float.compare(that.boxWidth, boxWidth) != 0) return false;
        if (Float.compare(that.boxHeight, boxHeight) != 0) return false;
        if (Float.compare(that.speedDeltaTop, speedDeltaTop) != 0) return false;
        if (Float.compare(that.speedDeltaSides, speedDeltaSides) != 0) return false;
        if (Float.compare(that.speedDeltaBottom, speedDeltaBottom) != 0) return false;
        if (Float.compare(that.barrierPosX, barrierPosX) != 0) return false;
        if (Float.compare(that.barrierWidth, barrierWidth) != 0) return false;
        if (Float.compare(that.holePosY, holePosY) != 0) return false;
        if (Float.compare(that.holeHeight, holeHeight) != 0) return false;
        if (Float.compare(that.speedLoss, speedLoss) != 0) return false;
        if (Float.compare(that.particleRadius, particleRadius) != 0) return false;
        return Float.compare(that.g, g) == 0;
    }

    @Override
    public int hashCode() {
        int result = (boxWidth != +0.0f ? Float.floatToIntBits(boxWidth) : 0);
        result = 31 * result + (boxHeight != +0.0f ? Float.floatToIntBits(boxHeight) : 0);
        result = 31 * result + (speedDeltaTop != +0.0f ? Float.floatToIntBits(speedDeltaTop) : 0);
        result = 31 * result + (speedDeltaSides != +0.0f ? Float.floatToIntBits(speedDeltaSides) : 0);
        result = 31 * result + (speedDeltaBottom != +0.0f ? Float.floatToIntBits(speedDeltaBottom) : 0);
        result = 31 * result + (barrierPosX != +0.0f ? Float.floatToIntBits(barrierPosX) : 0);
        result = 31 * result + (barrierWidth != +0.0f ? Float.floatToIntBits(barrierWidth) : 0);
        result = 31 * result + (holePosY != +0.0f ? Float.floatToIntBits(holePosY) : 0);
        result = 31 * result + (holeHeight != +0.0f ? Float.floatToIntBits(holeHeight) : 0);
        result = 31 * result + (speedLoss != +0.0f ? Float.floatToIntBits(speedLoss) : 0);
        result = 31 * result + (particleRadius != +0.0f ? Float.floatToIntBits(particleRadius) : 0);
        result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
        return result;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private float boxWidth;
        private float boxHeight;
        private float speedDeltaTop;
        private float speedDeltaSides;
        private float speedDeltaBottom;
        private float barrierPosX;
        private float barrierWidth;
        private float holePosY;
        private float holeHeight;
        private float speedLoss;
        private float particleRadius;
        private float g;

        public Builder setBoxSize(final float width, final float height) {
            boxWidth = width;
            boxHeight = height;

            return this;
        }

        public Builder setSpeedDelta(final float top, final float sides, final float bottom) {
            speedDeltaTop = top;
            speedDeltaSides = sides;
            speedDeltaBottom = bottom;

            return this;
        }

        public Builder setBarrier(final float posX, final float width) {
            barrierPosX = posX;
            barrierWidth = width;

            return this;
        }

        public Builder setHole(final float posY, final float height) {
            holePosY = posY;
            holeHeight = height;

            return this;
        }

        public Builder setSpeedLoss(final float speedLoss) {
            this.speedLoss = speedLoss;

            return this;
        }

        public Builder setParticleRadius(final float particleRadius) {
            this.particleRadius = particleRadius;

            return this;
        }

        public Builder setG(final float g) {
            this.g = g;

            return this;
        }

        public float getBoxWidth() {
            return boxWidth;
        }

        public float getBoxHeight() {
            return boxHeight;
        }

        public float getSpeedDeltaTop() {
            return speedDeltaTop;
        }

        public float getSpeedDeltaSides() {
            return speedDeltaSides;
        }

        public float getSpeedDeltaBottom() {
            return speedDeltaBottom;
        }

        public float getBarrierPosX() {
            return barrierPosX;
        }

        public float getBarrierWidth() {
            return barrierWidth;
        }

        public float getHolePosY() {
            return holePosY;
        }

        public float getHoleHeight() {
            return holeHeight;
        }

        public float getSpeedLoss() {
            return speedLoss;
        }

        public float getParticleRadius() {
            return particleRadius;
        }

        public float getG() {
            return g;
        }

        public WorldSettings build() {
            return new WorldSettings(this);
        }
    }
}
