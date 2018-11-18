package view.command;

import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * UIchoice
 *
 * Set of Languages Drop Bar on the UILog
 * Scene change between History and Main Simulation scene
 *
 * @author Amy Kim
 */

public class UIchoice {
    private VBox root;
    private ComboBox<String> myComboBox;
    public static final int ICON_SIZE = 50;
    private final String switchimg = "Commandhistory.png";
    private ImageView sceneBtn;

    public UIchoice(){
        this.makeDropbar();
        this.makeBtn();
        root = new VBox(12);
        root.getChildren().add(myComboBox);
        root.getChildren().add(sceneBtn);
        root.getStyleClass().add("settings-wrapper");
    }

    private void makeDropbar(){
        myComboBox = new ComboBox<>();
        myComboBox.getItems().addAll("English","Chinese","French","German","Italian", "Portuguese", "Russian", "Spanish", "Urdu");
        myComboBox.setValue("English");
        myComboBox.getStyleClass().add("langbox-wrapper");
    }

    public void registerListener(Consumer<String> languageHandler) {
        myComboBox.setOnAction(e -> languageHandler.accept(myComboBox.getValue()));
    }

    private void makeBtn() {
        var switchScene = new Image(this.getClass().getClassLoader().getResourceAsStream(switchimg));
        sceneBtn = new ImageView(switchScene);
        sceneBtn.setFitWidth(ICON_SIZE);
        sceneBtn.setFitHeight(ICON_SIZE);
        sceneBtn.getStyleClass().add("scenebtn");
    }

    public ImageView getSceneBtn(){ return sceneBtn; }

    public VBox getMyDropbar() {
        return root;
    }
}
