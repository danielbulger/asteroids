package com.danielbulger.asteroids.entity;

import com.danielbulger.asteroids.Collision;
import com.danielbulger.asteroids.Game;
import com.danielbulger.asteroids.Vector2;

import java.awt.*;

public class Bullet implements Entity {

	private static final long TIME_TO_LIVE = 3_000;

	private static final int DEFAULT_RADIUS = 1;

	private static final double VELOCITY = 3D;

	private final Game game;

	private final Vector2 position;

	private final Vector2 velocity;

	private final int radius = DEFAULT_RADIUS;

	private final long creation = System.currentTimeMillis();

	public Bullet(final Game game, final double x, final double y, final double velocityX, final double velocityY) {
		this.game = game;
		this.position = new Vector2(x, y);
		this.velocity = new Vector2(velocityX, velocityY).normalise().multiply(VELOCITY);
	}

	public Bullet(final Game game, final Vector2 position, final Vector2 velocity) {
		this(game, position.getX(), position.getY(), velocity.getX(), velocity.getY());
	}

	@Override
	public void update() {
		this.position.add(this.velocity);
		this.position.wrap(0, 0, game.getWidth(), game.getHeight());
	}

	@Override
	public void render(Graphics2D graphics) {

		graphics.translate(position.getX() - radius, position.getY() - radius);

		graphics.setColor(Color.RED);
		final int diameter = radius * 2;

		graphics.drawOval(0, 0, diameter, diameter);
	}

	public boolean checkCollision(Asteroid asteroid) {
		return Collision.testCircle(
			position, radius, asteroid.getPosition(), asteroid.getRadius()
		);
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	public boolean isDead() {
		return System.currentTimeMillis() - creation >= TIME_TO_LIVE;
	}
}
