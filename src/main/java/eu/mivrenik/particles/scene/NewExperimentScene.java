package eu.mivrenik.particles.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * New experiment scene.
 */
public class NewExperimentScene extends Scene {

	private NewExperimentScene(Parent root) {
		super(root);
	}

	/**
	 * Use this method to instantiate a new scene.
	 *
	 * @return Current class instance.
	 */
	public static NewExperimentScene newInstance() {
		String fxmlFile = "/fxml/scene_new_experiment.fxml";
		FXMLLoader loader = new FXMLLoader();
		Parent rootNode;
		try {
			rootNode = (Parent) loader.load(NewExperimentScene.class.getResourceAsStream(fxmlFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new NewExperimentScene(rootNode);
	}
}
