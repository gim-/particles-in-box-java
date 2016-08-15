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

import java.io.Serializable;

public class Particle implements Serializable {
    private static final long serialVersionUID = -957990242199617646L;

    private double posX;
    private double posY;
    private double velocityX;
    private double velocityY;
    private int id;

    public Particle(final int id,
                    final double posX,
                    final double posY,
                    final double velocityX,
                    final double velocityY) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public Particle(final Particle source) {
        this.id = source.getId();
        this.posX = source.getPosX();
        this.posY = source.getPosY();
        this.velocityX = source.getVelocityX();
        this.velocityY = source.getVelocityY();
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int getId() {
        return id;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Particle move(final double x, final double y) {
        posX = x;
        posY = y;

        return this;
    }

    public Particle setVelocity(final double vX, final double vY) {
        velocityX = vX;
        velocityY = vY;

        return this;
    }

    public double getSpeed() {
        return Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    }

    public double getDistanceTo(final Particle other) {
        return Math.sqrt(
                (posX - other.posX) * (posX - other.posX)
                + (posY - other.posY) * (posY - other.posY)
        );
    }

    public boolean approaches(final Particle other) {
        double dVx, dVy;

        if (posX < other.posX) {
            dVx = velocityX - other.velocityX;
        } else {
            dVx = other.velocityX - velocityX;
        }

        if (posY < other.posY) {
            dVy = velocityY - other.velocityY;
        } else {
            dVy = other.velocityY - velocityY;
        }

        return dVx > 0 || dVy > 0;
    }

    public boolean overlaps(final Particle other, final double particleR) {
        return getDistanceTo(other) < 2 * particleR;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Particle particle = (Particle) o;

        if (Double.compare(particle.posX, posX) != 0) return false;
        if (Double.compare(particle.posY, posY) != 0) return false;
        if (Double.compare(particle.velocityX, velocityX) != 0) return false;
        if (Double.compare(particle.velocityY, velocityY) != 0) return false;
        return id == particle.id;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(posX);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(posY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(velocityX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(velocityY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "Particle (id: %6d, pos: (%6.2f; %6.2f), velocity: (%6.2f; %6.2f))",
                getId(),
                getPosX(),
                getPosY(),
                getVelocityX(),
                getVelocityY()
        );
    }

    public static class Builder {
        private double posX;
        private double posY;
        private double velocityX;
        private double velocityY;
        private int id;

        public Builder setId(final int id) {
            this.id = id;

            return this;
        }

        public Builder setPosition(final double posX, final double posY) {
            this.posX = posX;
            this.posY = posY;

            return this;
        }

        public Builder setVelocity(final double velocityX, final double velocityY) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;

            return this;
        }

        public double getPosX() {
            return posX;
        }

        public double getPosY() {
            return posY;
        }

        public double getVelocityX() {
            return velocityX;
        }

        public double getVelocityY() {
            return velocityY;
        }

        public double getId() {
            return id;
        }

        public Particle build() {
            return new Particle(id, posX, posY, velocityX, velocityY);
        }
    }
}


