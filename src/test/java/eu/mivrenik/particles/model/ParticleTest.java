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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class ParticleTest {
    private class ParticleGenerator {
        private int upperBoundId;
        private double lowerBoundCoordinate;
        private double upperBoundCoordinate;
        private double lowerBoundVelocity;
        private double upperBoundVelocity;
        Random rand = new Random();

        ParticleGenerator(int upperBoundId,
                          double lowerBoundCoordinate, double upperBoundCoordinate,
                          double lowerBoundVelocity, double upperBoundVelocity) {
            this.upperBoundId = upperBoundId;
            this.lowerBoundCoordinate = lowerBoundCoordinate;
            this.upperBoundCoordinate = upperBoundCoordinate;
            this.lowerBoundVelocity = lowerBoundVelocity;
            this.upperBoundVelocity = upperBoundVelocity;
        }

        public void setCoordinatesBound(double lowerBoundCoordinate,
                                        double upperBoundCoordinate) {
            this.lowerBoundCoordinate = lowerBoundCoordinate;
            this.upperBoundCoordinate = upperBoundCoordinate;
        }

        public void setVelocityBound(double lowerBoundVelocity,
                                     double upperBoundVelocity) {
            this.lowerBoundVelocity = lowerBoundVelocity;
            this.upperBoundVelocity = upperBoundVelocity;
        }

        Particle nextParticle() {
            int id = rand.nextInt(upperBoundId);
            double x = generateDouble(lowerBoundCoordinate, upperBoundCoordinate),
                    y = generateDouble(lowerBoundCoordinate, upperBoundCoordinate),
                    vX = generateDouble(lowerBoundVelocity, upperBoundVelocity),
                    vY = generateDouble(lowerBoundVelocity, upperBoundVelocity);
            return new Particle(id, x, y, vX, vY);
        }

        Particle nextParticleSpecifiedCoordinates(double x, double y) {
            int id = rand.nextInt(upperBoundId);
            double vX = generateDouble(lowerBoundVelocity, upperBoundVelocity),
                    vY = generateDouble(lowerBoundVelocity, upperBoundVelocity);
            return new Particle(id, x, y, vX, vY);
        }
    }

    double eps = 1E-10;
    double particleR = 1.0;
    Random random = new Random();
    ParticleGenerator particleGenerator;

    private double generateDouble(double lowerBound, double upperBound) {
        return random.nextDouble() * (upperBound - lowerBound) + lowerBound;
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        int upperBoundId = 20;
        double lowerBoundCoordinate = -10;
        double upperBoundCoordinate = 10;
        double lowerBoundVelocity = -5;
        double upperBoundVelocity = 5;
        particleGenerator = new ParticleGenerator(upperBoundId,
                                                  lowerBoundCoordinate, upperBoundCoordinate,
                                                  lowerBoundVelocity, upperBoundVelocity);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void particlesWithDifferentIdAreNotEqual() throws Exception {
        Particle p1 = new Particle(1, 0.0, 0.0, 0.0, 0.0);
        Particle p2 = new Particle(2, 0.0, 0.0, 0.0, 0.0);

        assertNotEquals(p1, p2);
    }

    @Test
    public void moveParticle() throws Exception {
        Particle particle = particleGenerator.nextParticle();

        double newX = generateDouble(-10, 10);
        double newY = generateDouble(-10, 10);

        particle.move(newX, newY);
        assertEquals(newX, particle.getPosX(), eps);
        assertEquals(newY, particle.getPosY(), eps);
    }

    @Test
    public void changeVelocity() throws Exception {
        Particle particle = particleGenerator.nextParticle();

        double newVx = generateDouble(-5, 5);
        double newVy = generateDouble(-5, 5);

        particle.setVelocity(newVx, newVy);
        assertEquals(newVx, particle.getVelocityX(), eps);
        assertEquals(newVy, particle.getVelocityY(), eps);
    }

    @Test
    public void distanceBetweenSpacedParticles() throws Exception {
        double newX = generateDouble(-10, 10);
        double newY = generateDouble(-10, 10);
        int coef = random.nextInt(20) + 50;

        Particle particle1 = particleGenerator.nextParticleSpecifiedCoordinates(newX, newY);
        Particle particle2 = particleGenerator.nextParticleSpecifiedCoordinates(newX * coef, newY * coef);
        double distance = Math.sqrt(Math.pow((coef - 1) * newX, 2.0) + Math.pow((coef - 1) * newY, 2.0));

        assertEquals(distance, particle1.getDistanceTo(particle2), eps);
    }

    @Test
    public void distanceToOurself() throws Exception {
        Particle particle = particleGenerator.nextParticle();

        assertEquals(0.0, particle.getDistanceTo(particle), eps);
    }

    @Test
    public void distanceSymmetry() throws Exception {
        Particle particle1 = particleGenerator.nextParticle();
        Particle particle2 = particleGenerator.nextParticle();

        assertEquals(particle1.getDistanceTo(particle2), particle2.getDistanceTo(particle1), eps);
    }

    @Test
    public void distanceBetweenParticlesWithSameCoordinates() throws Exception {
        double newX = generateDouble(-10, 10);
        double newY = generateDouble(-10, 10);

        Particle particle1 = particleGenerator.nextParticleSpecifiedCoordinates(newX, newY);
        Particle particle2 = particleGenerator.nextParticleSpecifiedCoordinates(newX, newY);

        assertEquals(0.0, particle1.getDistanceTo(particle2), eps);
    }

    @Test
    public void convergenceSymmetry() throws Exception {
        Particle particle1 = particleGenerator.nextParticle();
        Particle particle2 = particleGenerator.nextParticle();

        assertEquals(particle1.approaches(particle2), particle2.approaches(particle1));
    }

    @Test
    public void overlappingSymmetry() throws Exception {
        Particle particle1 = particleGenerator.nextParticle();
        Particle particle2 = particleGenerator.nextParticle();

        assertEquals(particle1.overlaps(particle2, particleR), particle2.overlaps(particle1, particleR));
    }

    @Test
    public void overlappingWhenDistLargerThan2R() throws Exception {
        // lower bound (1) is immutable for this test
        double coef = generateDouble(1, 25);

        double newX1 = generateDouble(-10, 10);
        double newY1 = generateDouble(-10, 10);
        double newX2 = newX1 + Math.sqrt(2) * coef * particleR;
        double newY2 = newY1 + Math.sqrt(2) * coef * particleR;

        Particle particle1 = particleGenerator.nextParticleSpecifiedCoordinates(newX1, newY1);
        Particle particle2 = particleGenerator.nextParticleSpecifiedCoordinates(newX2, newY2);

        assertFalse(particle1.overlaps(particle2, particleR));
    }

    @Test
    public void overlappingWhenDistLessThan2R() throws Exception {
        // lower and upper bounds are immutable for this test
        double coef = generateDouble(0, 1);

        double newX1 = generateDouble(-10, 10);
        double newY1 = generateDouble(-10, 10);
        double newX2 = newX1 + Math.sqrt(2) * coef * particleR;
        double newY2 = newY1 + Math.sqrt(2) * coef * particleR;

        Particle particle1 = particleGenerator.nextParticleSpecifiedCoordinates(newX1, newY1);
        Particle particle2 = particleGenerator.nextParticleSpecifiedCoordinates(newX2, newY2);

        assertTrue(particle1.overlaps(particle2, particleR));
    }

    @Test
    public void overlappingWithItself() throws Exception {
        Particle particle = particleGenerator.nextParticle();

        assertTrue(particle.overlaps(particle, particleR));
    }

    @Test
    public void serializationReadAndWriteCheck() throws Exception {
        Particle particle = particleGenerator.nextParticle();
        int sourceId = particle.getId();

        File tempFile = testFolder.newFile("test.txt");
        FileOutputStream out = new FileOutputStream(tempFile);
        FileInputStream in = new FileInputStream(tempFile);
        ObjectOutputStream outObj = new ObjectOutputStream(out);
        ObjectInputStream inObj = new ObjectInputStream(in);

        outObj.writeObject(particle);
        outObj.close();
        Particle readedParticle = (Particle)inObj.readObject();
        inObj.close();

        assertTrue(particle.equals(readedParticle));
        assertEquals(sourceId, readedParticle.getId());
    }
}