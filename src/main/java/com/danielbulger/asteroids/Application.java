package com.danielbulger.asteroids;

import com.danielbulger.neat.Evolution;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.evaluate.SpeciesDistanceClassifier;
import com.danielbulger.neat.select.WeightedFitnessSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class Application extends Canvas {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

	private static Evolution getEvolution() throws Exception {

		final Properties properties = new Properties();
		final URL url = Application.class.getClassLoader().getResource("neat.properties");

		if (url == null) {
			throw new Exception("No properties file founds");
		}

		properties.load(new FileReader(url.getFile()));

		return new Evolution(
			properties,
			new SpeciesDistanceClassifier(1.5f, 1.5f, 0.8f, 1.0f),
			new WeightedFitnessSelect()
		);
	}

	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	public static void main(final String[] args) throws Exception {

		final Application application = new Application();
		application.setSize(WIDTH, HEIGHT);

		final JFrame frame = new JFrame("Asteroids");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(application);
		frame.pack();
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setVisible(true);

		application.run(getEvolution());
	}

	private Game activeGame = null;

	private volatile boolean running = true;

	private Genome best = null;

	private void runTrainingGames(Evolution evolution) {

		final int numWorkers = Runtime.getRuntime().availableProcessors();

		final ExecutorService workers = Executors.newFixedThreadPool(numWorkers);

		final Phaser phaser = new Phaser(1);

		while (running) {

			final List<Genome> genomes = evolution.getGenomes();

			phaser.bulkRegister(genomes.size());

			for (final Genome genome : genomes) {
				workers.submit(new BrainGame(phaser, genome));
			}

			// Wait until all other genomes have finished playing.
			phaser.arriveAndAwaitAdvance();

			best = new Genome(evolution.evolve());

			LOG.info("Population completed best: {}", best);
		}

		workers.shutdownNow();
	}

	public void run(Evolution evolution) {

		final long fps = 60;
		final long milliPerFrame = 1_000 / fps;

		final List<Genome> genomes = evolution.getGenomes();

		best = genomes.get(0);

		final Thread thread = new Thread(() -> {
			runTrainingGames(evolution);
		});

		thread.start();

		while (running) {

			if (activeGame == null || activeGame.isGameOver()) {
				activeGame = new Game(WIDTH, HEIGHT, best);
			}

			final long now = System.currentTimeMillis();

			synchronized (this) {
				activeGame.update();
			}

			repaint();

			final long time = milliPerFrame - (System.currentTimeMillis() - now);

			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException exception) {
					break;
				}
			}
		}

	}

	@Override
	public void paint(final Graphics g) {
		if (activeGame == null) {
			return;
		}

		// Make sure we don't render while an update is happening.
		synchronized (this) {
			final Graphics2D graphics = (Graphics2D) g;

			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, WIDTH, HEIGHT);

			activeGame.render(graphics);
		}

		Toolkit.getDefaultToolkit().sync();
	}

	private static class BrainGame implements Runnable {

		private final Phaser phaser;

		private final Genome genome;

		private BrainGame(Phaser phaser, Genome genome) {
			this.phaser = phaser;
			this.genome = genome;
		}

		@Override
		public void run() {

			try {

				final Game game = new Game(WIDTH, HEIGHT, genome);

				while (!game.isGameOver()) {
					game.update();
				}

			} finally {
				phaser.arriveAndDeregister();
			}
		}
	}
}
