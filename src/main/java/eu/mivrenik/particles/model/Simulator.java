package eu.mivrenik.particles.model;

import java.util.Arrays;
import java.util.Random;

public class Simulator {
    private ExperimentSettings experimentSettings;
    private Random random = new Random();

    public Simulator(final ExperimentSettings experimentSettings) {
        this.experimentSettings = experimentSettings;
    }

    private double generateDouble(final double lowerBound, final double upperBound) {
        return random.nextDouble() * (upperBound - lowerBound) + lowerBound;
    }

    public ExperimentState initialDistribution() {
        ExperimentState.Builder experimentStateBuilder = ExperimentState.newBuilder();
        Particle.Builder particleBuilder = new Particle.Builder();
        int particlesNum = experimentSettings.getParticleCountLeft() + experimentSettings.getParticleCountRight();
        Particle[] particles = new Particle[particlesNum];
        double angle;
        boolean bTouching;
        double currX;
        double currY;
        double currVx;
        double currVy;

        // left part
        for (int i = 0; i < experimentSettings.getParticleCountLeft(); i++) {
            do {
                bTouching = false;

                currX = generateDouble(0 + experimentSettings.getParticleRadius(),
                                       experimentSettings.getBarrierPosX() - experimentSettings.getBarrierWidth() / 2
                                                                           - experimentSettings.getParticleRadius());
                currY = generateDouble(0 + experimentSettings.getParticleRadius(),
                                       experimentSettings.getBoxHeight() - experimentSettings.getParticleRadius());

                for (int j = 0; j < i; j++) {
                    if (particles[i].overlaps(particles[j], experimentSettings.getParticleRadius())) {
                        bTouching = true;
                        break;
                    }
                }
            } while (bTouching);

            angle = generateDouble(0, Math.PI * 2);
            currVx = experimentSettings.getInitialSpeed() * Math.cos(angle);
            currVy = experimentSettings.getInitialSpeed() * Math.sin(angle);

            particleBuilder.setPosition(currX, currY)
                    .setVelocity(currVx, currVy)
                    .setId(2 * i);
            particles[i] = particleBuilder.build();
        }

        // right part
        for (int i = experimentSettings.getParticleCountLeft(); i < particlesNum; i++) {
            do {
                bTouching = false;

                currX = generateDouble(experimentSettings.getBarrierPosX() + experimentSettings.getBarrierWidth() / 2
                                                                           + experimentSettings.getParticleRadius(),
                                       0 + experimentSettings.getBoxWidth());
                currY = generateDouble(0 + experimentSettings.getParticleRadius(),
                                       experimentSettings.getBoxHeight() - experimentSettings.getParticleRadius());

                for (int j = experimentSettings.getParticleCountLeft(); j < i; j++) {
                    if (particles[i].overlaps(particles[j], experimentSettings.getParticleRadius())) {
                        bTouching = true;
                        break;
                    }
                }
            } while (bTouching);

            angle = generateDouble(0, Math.PI * 2);
            currVx = experimentSettings.getInitialSpeed() * Math.cos(angle);
            currVy = experimentSettings.getInitialSpeed() * Math.sin(angle);

            particleBuilder.setPosition(currX, currY)
                           .setVelocity(currVx, currVy)
                           .setId(2 * i + 1);
            particles[i] = particleBuilder.build();
        }

        return experimentStateBuilder.setParticles(particles).setSettings(experimentSettings).setTime(0).build();

    }

    public ExperimentState nextTimeStep(final ExperimentState experimentState, final long deltaTime) {
        double x;
        double y;
        double vX;
        double vY;
        double dX;
        double dY;
        double dVx;
        double dVy;
        double distance;
        double sin;
        double cos;
        double needToMove;
        double v1X;
        double v1Y;
        double v2X;
        double v2Y;

        double particleRadius = experimentSettings.getParticleRadius();
        int particlesNum = experimentSettings.getParticleCountLeft() + experimentSettings.getParticleCountRight();
        double factor = Math.sqrt(1 - experimentSettings.getSpeedLoss());
        ExperimentState.Builder experimentStateBuilder = ExperimentState.newBuilder();
        Particle[] particles = experimentState.getParticles();
        Particle[] movedParticles =  new Particle[particles.length];

        double boxLeftSideBound = 0;
        double boxRightSideBound = experimentSettings.getBoxWidth();
        double boxTopBound = experimentSettings.getBoxHeight();
        double boxBottomBound = 0;
        double boxLeftSideParticleBound = boxLeftSideBound + particleRadius;
        double boxRightSideParticleBound = boxRightSideBound - particleRadius;
        double boxTopParticleBound = boxTopBound - particleRadius;
        double boxBottomParticleBound = boxBottomBound + particleRadius;

        double holeLeftSideBound = experimentSettings.getBarrierPosX() - experimentSettings.getBoxWidth() / 2;
        double holeRightSideBound = experimentSettings.getBarrierPosX() + experimentSettings.getBoxWidth() / 2;
        double holeTopBound = experimentSettings.getHolePosY() + experimentSettings.getHoleHeight() / 2;
        double holeBottomBound = experimentSettings.getHolePosY() - experimentSettings.getHoleHeight() / 2;
        double holeLeftSideParticleBound = holeLeftSideBound - particleRadius;
        double holeRightSideParticleBound = holeRightSideBound - particleRadius;
        double holeTopParticleBound = holeTopBound - particleRadius;
        double holeBottomParticleBound = holeBottomBound - particleRadius;

        // move particles
        for (int i = 0; i < particles.length; i++) {
            movedParticles[i] = new Particle(particles[i]).move(deltaTime, experimentSettings.getG());
        }

        // particle's collision
        Arrays.sort(movedParticles,
                   (p1, p2) -> p1.getPosY() < p2.getPosY() ? -1 : p1.getPosY() > p2.getPosY() ? 1 : 0);

        for (int i = 0; i < particlesNum; i++) {
            for (int j = i + 1; j < particlesNum; j++) {
                if (movedParticles[j].getPosY() - movedParticles[i].getPosY() > 2 * particleRadius) break;
                if (movedParticles[i].overlaps(movedParticles[j], particleRadius)
                        && movedParticles[i].approaches(movedParticles[j])) {
                    dX = movedParticles[i].getPosX() - movedParticles[j].getPosX();
                    dY = movedParticles[i].getPosY() - movedParticles[j].getPosY();
                    distance = Math.sqrt(dX * dX + dY * dY);
                    cos = dX / distance;
                    sin = dY / distance;
                    dVx = movedParticles[i].getVelocityX() - movedParticles[j].getVelocityX();
                    dVy = movedParticles[i].getVelocityY() - movedParticles[j].getVelocityY();

                    v1X = dVx * sin * sin - dVy * sin * cos + movedParticles[j].getVelocityX();
                    v1Y = dVy * cos * cos - dVx * sin * cos + movedParticles[j].getVelocityY();
                    v2X = dVx * cos * cos + dVy * sin * cos + movedParticles[j].getVelocityX();
                    v2Y = dVy * sin * sin + dVx * sin * cos + movedParticles[j].getVelocityY();

                    movedParticles[i] = movedParticles[i].setVelocity(v1X * factor, v1Y * factor);
                    movedParticles[j] = movedParticles[j].setVelocity(v2X * factor, v2Y * factor);

                    // overlapped particles
                    if (distance * distance < 4 * particleRadius * particleRadius) {
                        needToMove = 2 * particleRadius - distance;

                        if (dY > 0) {
                            movedParticles[i] = movedParticles[i]
                                    .setPosition(movedParticles[i].getPosX() + needToMove * cos,
                                                 movedParticles[i].getPosY() + needToMove * sin);
                        } else {
                            movedParticles[j] = movedParticles[j]
                                    .setPosition(movedParticles[j].getPosX() - needToMove * cos,
                                                 movedParticles[j].getPosY() - needToMove * sin);
                        }
                    }
                }
            }
        }

        // collision with geometry
        for (Particle particle : movedParticles) {
            x = particle.getPosX();
            y = particle.getPosY();
            vX = particle.getVelocityX();
            vY = particle.getVelocityY();

            if (y > boxTopParticleBound && vY > 0) {
                // box top
                y = boxTopParticleBound;
                vY = -vY - experimentSettings.getSpeedDeltaTop();
            } else if (y < boxBottomParticleBound && vY < 0) {
                // box bottom
                y = boxBottomParticleBound;
                vY = -vY + experimentSettings.getSpeedDeltaBottom();
            }

            if (x < boxLeftSideParticleBound && vX < 0) {
                // box left side
                x = boxLeftSideParticleBound;
                vX = -vX + experimentSettings.getSpeedDeltaSides();
            } else if (x > boxRightSideParticleBound && vX > 0) {
                // box right side
                x = boxRightSideParticleBound;
                vX = -vX - experimentSettings.getSpeedDeltaSides();
            } else if (x > holeLeftSideParticleBound && x < holeRightSideParticleBound) {
                // inside hole
                if (x > holeLeftSideBound && x < holeRightSideBound) {
                    if (y > holeTopParticleBound && vY > 0) {
                        // hole top
                        y = holeTopParticleBound;
                        vY = -vY - experimentSettings.getSpeedDeltaTop();
                    } else if (y < holeBottomParticleBound && vY < 0) {
                        // hole bottom
                        y = holeBottomParticleBound;
                        vY = -vY + experimentSettings.getSpeedDeltaBottom();
                    }
                } else if (y > holeTopBound || y < holeBottomBound) {
                    // around left barrier side
                    if (x < experimentSettings.getBarrierPosX() && vX > 0) {
                        x = holeLeftSideParticleBound;
                        vX = -vX - experimentSettings.getSpeedDeltaSides();
                    } else if (x > experimentSettings.getBarrierPosX() && vX < 0) {
                        // around right barrier side
                        x = holeRightSideParticleBound;
                        vX = -vX + experimentSettings.getSpeedDeltaSides();
                    }
                }
            }

            particle.setPosition(x, y).setVelocity(vX, vY);
        }

        return experimentStateBuilder
                .setParticles(movedParticles)
                .setSettings(experimentSettings)
                .setTime(experimentState.getTime() + deltaTime)
                .build();
    }
}
