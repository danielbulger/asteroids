package com.danielbulger.asteroids.entity;

import com.danielbulger.asteroids.Vector2;

import java.awt.*;

public interface Entity {

	void update();

	void render(final Graphics2D graphics);

	Vector2 getPosition();
}
