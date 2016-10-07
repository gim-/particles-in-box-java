package eu.mivrenik.particles.scene;

import java.io.IOException;

import eu.mivrenik.particles.controller.DemonstrationController;
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
    public static DemonstrationScene newInstance(final String filePath) {
        String fxmlFile = "/fxml/scene_demonstration.fxml";
        FXMLLoader loader = new FXMLLoader(
                NewExperimentScene.class.getResource(fxmlFile)
        );

        Parent rootNode;

        try {
            DemonstrationController controller = new DemonstrationController(filePath);
            loader.setController(controller);

            rootNode = (Parent) loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new DemonstrationScene(rootNode);
    }
}
