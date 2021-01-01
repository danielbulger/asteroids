package com.danielbulger.asteroids;

public class Collision {

	public static boolean testCircle(Vector2 c1, double r1, Vector2 c2, double r2) {
		final double distance = c2.distance(c1);

		return distance < (r1 + r2);
	}

	public static boolean testLineAndCircle(Vector2 circle, double radius, Vector2 l1, Vector2 l2) {
		if (isPointInCircle(circle, radius, l1) || isPointInCircle(circle, radius, l2)) {
			return true;
		}

		final double deltaX = l2.getX() - l1.getX();
		final double deltaY = l2.getY() - l1.getY();

		final double length = l1.distance(l2);
		final double dot = (
			((circle.getX() - l1.getX()) * deltaX) +
			((circle.getY() - l1.getY()) * deltaY)
		) / Math.pow(length, 2);

		final Vector2 closest = new Vector2(
			l1.getX() + (dot * deltaX),
			l1.getY() + (dot * deltaY)
		);

		if (!linePoint(l1, l2, closest)) {
			return false;
		}

		return closest.distance(circle) <= radius;

	}

	private static boolean isPointInCircle(Vector2 circle, double radius, Vector2 point) {
		final double dist = point.distance(circle);
		return dist <= radius;
	}

	private static boolean linePoint(Vector2 l1, Vector2 l2, Vector2 p) {
		final double d = p.distance(l1) + p.distance(l2);
		final double len = l2.distance(l1);

		final double error = 0.1;

		return d >= len - error && d <= len + error;
	}

	private Collision() {
	}
}
