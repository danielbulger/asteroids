package com.danielbulger.asteroids;

public class Vector2 {

	public static Vector2 fromAngle(double rad) {
		return new Vector2(
			Math.cos(rad),
			Math.sin(rad)
		);
	}

	private double x, y;

	public Vector2() {
		this(0, 0);
	}

	public Vector2(double x, double y) {
		this.set(x, y);
	}

	public double distance(Vector2 other) {
		return Math.sqrt(distanceSquared(other));
	}

	public double distanceSquared(Vector2 other) {

		final double dx = (other.x - x) * (other.x - x);

		final double dy = (other.y - y) * (other.y - y);

		return dx + dy;
	}

	public Vector2 add(Vector2 other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	public Vector2 multiply(final double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}

	public Vector2 normalise() {
		final double mag = length();
		x /= mag;
		y /= mag;
		return this;
	}

	public Vector2 limit(double val) {
		this.x = Math.max(Math.min(x, val), -val);
		this.y = Math.max(Math.min(y, val), -val);
		return this;
	}

	public void wrap(double minX, double minY, double maxX, double maxY) {
		if (x < minX) {
			x = maxX;
		} else if (x > maxX) {
			x = minX;
		}

		if (y < minY) {
			y = maxY;
		} else if (y > maxY) {
			y = minY;
		}
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void set(Vector2 other) {
		this.set(other.x, other.y);
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
