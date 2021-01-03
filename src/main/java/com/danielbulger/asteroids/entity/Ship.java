package com.danielbulger.asteroids.entity;

import com.danielbulger.asteroids.Game;
import com.danielbulger.asteroids.Vector2;

import java.awt.*;

public class Ship implements Entity {

	private static final long RELOAD_TIME = 4;

	private static final double DEFAULT_ROTATION = Math.toRadians(-90D);

	private static final double ROTATION_PER_FRAME = Math.toRadians(5D);

	private static final long IMMUNITY_TIME = 88;

	private static final double MAX_SPEED = 2D;

	private final Vector2 position;

	private final Vector2 velocity = new Vector2();

	private final Vector2 acceleration = new Vector2();

	private final Vector2[] points = new Vector2[3 * 2];

	private double rotation = DEFAULT_ROTATION, spin = 0.0;

	private final Game game;

	private boolean shooting = false;

	private long lastShot = 0;

	private long immunity = 0;

	public Ship(Game game, double x, double y) {
		this.game = game;
		this.position = new Vector2(x, y);
	}

	@Override
	public void update() {
		updateRotation();
		updatePosition();
		updatePoints();
		shoot();
	}

	private void updateRotation() {
		rotation += spin;
	}

	private void updatePosition() {
		velocity.add(acceleration).limit(MAX_SPEED);

		position.add(velocity);

		position.wrap(0, 0, game.getWidth(), game.getHeight());
	}

	public void die() {
		position.set(game.getWidth() / 2.0, game.getHeight() / 2.0);
		acceleration.set(0.0, 0.0);
		velocity.set(0.0, 0.0);
		rotation = DEFAULT_ROTATION;
		spin = 0.0d;
		lastShot = 0;
		immunity = game.getCurrentTick();
	}

	public void startShooting() {
		shooting = true;
	}

	public void stopShooting() {
		shooting = false;
	}

	private void shoot() {
		if (!shooting || !canShoot()) {
			return;
		}

		final Bullet bullet = new Bullet(
			game, position, Vector2.fromAngle(rotation)
		);

		game.addBullet(bullet);

		lastShot = game.getCurrentTick();
	}

	@Override
	public void render(Graphics2D graphics) {

		if(isImmune() && Math.random() > 0.5) {
			return;
		}

		graphics.translate(position.getX(), position.getY());

		graphics.rotate(rotation);

		graphics.setColor(Color.WHITE);

		final int len = getLineLength();

		graphics.drawLine(-len, -len, len, 0);
		graphics.drawLine(-len, len, len, 0);
		graphics.drawLine(-len, -len, -len, len);
	}

	public void boost() {
		acceleration.set(Vector2.fromAngle(rotation));
		acceleration.normalise();
	}

	public void boostOff() {
		acceleration.set(0.0, 0.0);
	}

	public void stopRotation() {
		spin = 0;
	}

	public void rotateLeft() {
		spin = -ROTATION_PER_FRAME;
	}

	public void rotateRight() {
		spin = ROTATION_PER_FRAME;
	}

	public double getRotation() {
		return rotation;
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	private void updatePoints() {

		if (points[0] == null) {
			for (int i = 0; i < points.length; ++i) {
				points[i] = new Vector2();
			}
		}

		final int length = getLineLength();

		points[0].set(position.getX() - length, position.getY() - length);
		points[1].set(position.getX() + length, position.getY());

		points[2].set(position.getX() - length, position.getY() + length);
		points[3].set(position.getX() + length, position.getY());

		points[4].set(position.getX() - length, position.getY() - length);
		points[5].set(position.getX() - length, position.getY() + length);
	}

	public Vector2[] getPoints() {
		return points;
	}

	public int getLineLength() {
		return 10;
	}

	public boolean isImmune() {
		return immunity >= 0 && (game.getCurrentTick() - immunity) <= IMMUNITY_TIME;
	}

	public boolean canShoot() {
		return game.getCurrentTick() - lastShot >= RELOAD_TIME;
	}
}
