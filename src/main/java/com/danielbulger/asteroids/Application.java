package com.danielbulger.asteroids;

import javax.swing.*;
import java.awt.*;

public class Application extends Canvas {

	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;

	public static void main(final String[] args) {

		final Application application = new Application();
		application.setSize(WIDTH, HEIGHT);

		final JFrame frame = new JFrame("Asteroids");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(application);
		frame.pack();
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setVisible(true);

		application.run();
	}

	private final Game game = new Game(WIDTH, HEIGHT);

	private volatile boolean running = true;

	public Application() {
		addKeyListener(game);
	}

	public void run() {

		final long fps = 60;
		final long milliPerFrame = 1_000 / fps;

		while (running) {
			final long now = System.currentTimeMillis();

			synchronized(this) {
				game.update();
			}

			repaint();

			final long time = milliPerFrame - (System.currentTimeMillis() - now);

			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException exception) {
					running = false;
				}
			}
		}

	}

	@Override
	public void paint(final Graphics g) {
		synchronized (this) {
			final Graphics2D graphics = (Graphics2D) g;

			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, WIDTH, HEIGHT);

			game.render(graphics);

			Toolkit.getDefaultToolkit().sync();
		}
	}
}
