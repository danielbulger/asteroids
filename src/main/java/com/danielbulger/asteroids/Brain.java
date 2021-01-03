package com.danielbulger.asteroids;

import com.danielbulger.asteroids.entity.Asteroid;
import com.danielbulger.asteroids.entity.Ship;
import com.danielbulger.neat.Genome;

import java.awt.*;
import java.util.Collection;

public class Brain {

	private static final double THRESHOLD = 0.8;

	private final Ship ship;

	private final Genome genome;

	private final GameState state;

	private final Vector2[] sight;

	public Brain(Genome genome, Ship ship, GameState state) {
		this.genome = genome;
		this.ship = ship;
		this.state = state;
		this.sight = new Vector2[genome.getNumInputs() - 1];
	}

	public void render(Graphics2D graphics) {

		graphics.setColor(Color.WHITE);

		for(final Vector2 vector : sight) {
			graphics.drawLine(
				(int) ship.getPosition().getX(),
				(int) ship.getPosition().getY(),
				(int) vector.getX(),
				(int) vector.getY()
			);
		}
	}

	public void update(Collection<Asteroid> asteroids) {
		if (genome == null) {
			return;
		}

		genome.setFitness(calculateFitness());

		performAction(observe(asteroids));
	}

	public float[] observe(Collection<Asteroid> asteroids) {

		final double segmentSize = (2 * Math.PI) / sight.length;

		// 1 input for the shoot status and 8 for the direction
		final float[] inputs = new float[1 + sight.length];

		inputs[0] = ship.canShoot() ? 1 : 0;

		final Vector2 position = new Vector2();

		for (int i = 1; i < inputs.length; ++i) {

			position.set(ship.getPosition());

			final Vector2 direction = Vector2.fromAngle(ship.getRotation() + ((i - 1) * segmentSize));

			final Asteroid result = castRay(direction, position, asteroids);

			sight[i - 1] = new Vector2(position);

			if (result != null) {
				inputs[i] = (float) ship.getPosition().distance(position);
			}
		}

		return genome.feedForward(inputs);
	}

	private Asteroid castRay(Vector2 direction, Vector2 position, Collection<Asteroid> asteroids) {
		while (Collision.testRectangle(position, 0, 0, state.getWidth(), state.getHeight())) {

			for (Asteroid asteroid : asteroids) {

				if (asteroid.checkCollision(position)) {
					return asteroid;
				}
			}

			position.add(direction);
		}

		return null;
	}

	public void performAction(float[] actions) {
		// 0 = shoot, 1 = boost, 2 = turn-left, 3 = turn-right

		if (actions[0] >= THRESHOLD) {
			ship.startShooting();
		} else {
			ship.stopShooting();
		}

		if (actions[1] >= THRESHOLD) {
			ship.boost();
		} else {
			ship.boostOff();
		}

		if (actions[2] < THRESHOLD && actions[3] < THRESHOLD) {
			ship.stopRotation();
		} else {
			if (actions[2] >= THRESHOLD) {
				ship.rotateLeft();
			}

			if (actions[3] >= THRESHOLD) {
				ship.rotateRight();
			}
		}
	}

	private float calculateFitness() {
		float fitness = state.getScore();
		// Factor in the amount of time the ship stayed alive for.
		fitness += state.getTicks();

		if (state.getShotsFired() > 0) {
			// The accuracy, this is to prevent the ship from spamming bullets without aiming.
			fitness += (float) state.getShotsHit() / (float) state.getShotsFired();
		}

		return fitness;
	}
}
