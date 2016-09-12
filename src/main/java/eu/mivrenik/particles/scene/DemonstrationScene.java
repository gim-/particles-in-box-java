package eu.mivrenik.particles.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class DemonstrationScene extends Scene {

    /**
     * Disallow custom external instantiation.
     *
     * @param root
     *            {@inheritDoc}
     */
    private DemonstrationScene(final Parent root) {
        super(root);
    }

    /**
     * Use this method to instantiate a new scene.
     *
     * @return Current class instance.
     */
    public static DemonstrationScene newInstance() {
        String fxmlFile = "/fxml/scene_demonstration.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode;
        try {
            rootNode = (Parent) loader.load(NewExperimentScene.class.getResourceAsStream(fxmlFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new DemonstrationScene(rootNode);
    }
}
