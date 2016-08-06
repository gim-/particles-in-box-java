package eu.mivrenik.particles;

import java.util.logging.Logger;

import eu.mivrenik.particles.scene.NewExperimentScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class that is used as entry point.
 */
public class AppEntryPoint extends Application {
	private static final Logger LOG = Logger.getLogger(AppEntryPoint.class.getName());

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	/**
	 * Initialise main view stage.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		LOG.info("Starting particles in box application");
		Scene scene = NewExperimentScene.newInstance();
		stage.setTitle("New experiment");
		stage.setScene(scene);
		stage.show();
		// Restrict resizing
		stage.setMinHeight(scene.getHeight());
		stage.setMinWidth(scene.getWidth());
	}
}
