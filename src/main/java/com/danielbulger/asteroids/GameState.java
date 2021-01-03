package com.danielbulger.asteroids;

public class GameState {

	private static final int START_NUM_LIVES = 3;
	private final int width, height;

	private int ticks = 0;
	private int lives = START_NUM_LIVES;
	private int shotsFired = 0;
	private int shotsHit = 0;
	private int score = 0;

	public GameState(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void addTick() {
		++ticks;
	}

	public void removeLife() {
		--lives;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public void addShotFired() {
		++shotsFired;
	}

	public void addShotHit() {
		++shotsHit;
	}

	public int getShotsFired() {
		return shotsFired;
	}

	public int getShotsHit() {
		return shotsHit;
	}

	public int getScore() {
		return score;
	}

	public int getLives() {
		return lives;
	}

	public int getTicks() {
		return ticks;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "GameState{" +
			"ticks=" + ticks +
			", lives=" + lives +
			", shotsFired=" + shotsFired +
			", shotsHit=" + shotsHit +
			", score=" + score +
			'}';
	}
}
