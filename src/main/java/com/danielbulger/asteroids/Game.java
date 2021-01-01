package com.danielbulger.asteroids;

import com.danielbulger.asteroids.entity.Asteroid;
import com.danielbulger.asteroids.entity.Bullet;
import com.danielbulger.asteroids.entity.Entity;
import com.danielbulger.asteroids.entity.Ship;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Game implements KeyListener {

	private static final int ASTEROID_START_HEALTH = 3;

	private final int width;
	private final int height;

	private final Ship ship;

	private final Random random = new Random();

	private final List<Asteroid> asteroids = new ArrayList<>();

	private final List<Bullet> bullets = new ArrayList<>();

	private final GameState state = new GameState();

	public Game(int width, int height) {
		this.width = width;
		this.height = height;
		this.ship = new Ship(this, width / 2.0, height / 2.0);
	}

	private void spawnAsteroid() {
		final double x = random.nextDouble() * width;
		final double y = random.nextDouble() * height;

		final double velocityX = random.nextDouble();
		final double velocityY = random.nextDouble();

		asteroids.add(new Asteroid(this, x, y, velocityX, velocityY, ASTEROID_START_HEALTH));
	}

	public void spawnAsteroids(int count) {
		for (int i = 0; i < count; ++i) {
			spawnAsteroid();
		}
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
		state.addShotFired();
	}

	public void update() {

		if (isGameOver()) {
			return;
		}

		if (asteroids.isEmpty()) {
			spawnAsteroids(5);
		}

		updateEntities();

		checkBulletCollision();

		// Don't bother with the collision detection if the ship is immune.
		if (!ship.isImmune()) {
			for (Asteroid asteroid : asteroids) {
				if (asteroid.checkCollision(ship)) {
					shipCollision();
				}
			}
		}

		System.out.println(state);
	}

	private void updateEntities() {
		ship.update();

		for (final Asteroid entity : asteroids) {
			entity.update();
		}

		for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
			final Bullet bullet = it.next();

			if (bullet.isDead()) {
				it.remove();
			} else {
				bullet.update();
			}
		}
	}

	private void shipCollision() {
		ship.die();
		bullets.clear();
		state.removeLife();
	}

	public boolean isGameOver() {
		return state.getLives() <= 0;
	}

	private Asteroid checkAsteroidCollision(Bullet bullet) {
		final Iterator<Asteroid> it = asteroids.iterator();

		while (it.hasNext()) {

			final Asteroid asteroid = it.next();

			if (bullet.checkCollision(asteroid)) {
				state.addShotHit();
				// The bullet has hit an asteroid so remove it from the world.
				it.remove();
				return asteroid;
			}
		}

		return null;
	}

	private void checkBulletCollision() {

		final Iterator<Bullet> it = bullets.iterator();

		final List<Asteroid> newAsteroids = new ArrayList<>();

		while (it.hasNext()) {
			final Bullet bullet = it.next();

			final Asteroid asteroid = checkAsteroidCollision(bullet);

			if (asteroid != null) {
				it.remove();
				// Add any children to the pending list of asteroids
				newAsteroids.addAll(asteroid.split());

				state.addScore(asteroid.getWorth());
			}
		}

		// Now all the iterations are finished, we can safely add the new asteroids in.
		asteroids.addAll(newAsteroids);
	}

	private void render(Graphics2D g, Entity entity) {
		final Graphics2D instance = (Graphics2D) g.create();
		entity.render(instance);
		instance.dispose();
	}

	public void render(Graphics2D graphics) {

		render(graphics, ship);

		for (final Entity entity : asteroids) {
			render(graphics, entity);
		}

		for (final Entity entity : bullets) {
			render(graphics, entity);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				ship.boost();
				break;

			case KeyEvent.VK_LEFT:
				ship.rotateLeft();
				break;

			case KeyEvent.VK_RIGHT:
				ship.rotateRight();
				break;

			case KeyEvent.VK_SPACE:
				ship.startShooting();
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				ship.boostOff();
				break;

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				ship.stopRotation();
				break;

			case KeyEvent.VK_SPACE:
				ship.stopShooting();
				break;
		}
	}
}
