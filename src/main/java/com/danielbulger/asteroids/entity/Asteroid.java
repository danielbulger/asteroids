package com.danielbulger.asteroids.entity;

import com.danielbulger.asteroids.Collision;
import com.danielbulger.asteroids.Game;
import com.danielbulger.asteroids.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Asteroid implements Entity {

	private static final Random RANDOM = new Random();

	private static final int CHILDREN_ON_DEATH = 2;

	private static final int RADIUS_SCALE_PER_HP = 15;

	private static final double BASE_VELOCITY = 2.25;

	private static final double VELOCITY_STEP = 0.25;

	private static final int HIGHEST_SCORE = 3;

	private static final int SCORE_PER_HP = 1;

	private final Game game;

	private final Vector2 position;

	private final Vector2 velocity;

	private final int health;

	private final int radius;

	public Asteroid(Game game, double x, double y, double velocityX, double velocityY, int health) {
		this.game = game;
		this.position = new Vector2(x, y);
		this.velocity = new Vector2(velocityX, velocityY).normalise();
		this.health = health;
		this.radius = health * RADIUS_SCALE_PER_HP;
		this.updateVelocity();
	}

	public Asteroid(Game game, Vector2 position, Vector2 velocity, int health) {
		this(game, position.getX(), position.getY(), velocity.getX(), velocity.getY(), health);
	}

	public Asteroid(Asteroid parent) {
		this(parent.game, parent.position, parent.velocity, parent.health - 1);
	}

	private void updateVelocity() {
		this.velocity.multiply(BASE_VELOCITY - (VELOCITY_STEP * health));
	}

	@Override
	public void update() {
		this.position.add(this.velocity);
		this.position.wrap(0, 0, game.getWidth(), game.getHeight());
	}

	public Collection<Asteroid> split() {

		// We can't get any smaller than this
		if (health == 1) {
			return Collections.emptyList();
		}

		final Collection<Asteroid> children = new ArrayList<>(CHILDREN_ON_DEATH);

		for (int i = 0; i < CHILDREN_ON_DEATH; ++i) {

			final Asteroid child = new Asteroid(this);

			child.velocity.set(
				child.velocity.getX() + (RANDOM.nextDouble() - 0.5),
				child.velocity.getY() + (RANDOM.nextDouble() - 0.5)
			);

			children.add(child);
		}

		return children;
	}

	@Override
	public void render(Graphics2D graphics) {

		final int diameter = radius * 2;

		// Since drawOval puts the center at the top left, we need to offset by radius
		// to ensure we are drawing correctly in the center.
		graphics.translate(position.getX() - radius, position.getY() - radius);

		graphics.setColor(Color.WHITE);

		graphics.drawOval(0, 0, diameter, diameter);
	}

	public boolean checkCollision(Vector2 point) {
		return Collision.testCircle(position, radius, point);
	}

	public boolean checkCollision(Ship ship) {

		final Vector2[] points = ship.getPoints();

		for (int i = 0; i < points.length; i += 2) {
			if (Collision.testLineAndCircle(position, radius, points[i], points[i + 1])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	public int getWorth() {
		return HIGHEST_SCORE - ((health - 1) * SCORE_PER_HP);
	}

	public int getRadius() {
		return radius;
	}
}
