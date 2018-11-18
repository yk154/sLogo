package gui;

import control.ControlSetup;
import control.Controller;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.UIsetup;
import gui.ViewModule;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Main Class
 *
 * Start Application for Slogo
 * TabPane object
 *   - generate new SLogo and move btw tabs.
 *
 * @author Amy Kim
 */

public class Main extends Application {
    public static final int WIDTH = 1220;
    public static final int HEIGHT = 750;
    public static final String STYLESHEET = "style.css";

    private ViewModule myTabs;
    private Stage myStage;

    @Override
    public void start(Stage stage) {
        myTabs = new ViewModule();
        myStage = stage;
        var myScene = new Scene(myTabs.getMyTab(), WIDTH, HEIGHT);

        myScene.getStylesheets().add(STYLESHEET);

        myStage.setScene(myScene);
        myStage.setTitle("SLogo!");

        myStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
