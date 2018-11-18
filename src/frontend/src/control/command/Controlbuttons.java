package control.command;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import view.command.UIbuttons;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Natalie
 */
public class Controlbuttons {
    private UIbuttons ui;
// TODO: Scroll Pane on DialogBox
    public Controlbuttons(UIbuttons ui){
        this.ui = ui;
        ui.getHelp().setOnAction(this::myHelpHandler);
        //ui.getSend().setTooltip()           // When hovered over, tooltip appears with currently available commands
    }

    private void myHelpHandler(ActionEvent e) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("HELP");
        alert.setHeaderText("See below for available commands.");
        ScrollPane helpGuide = new ScrollPane();
        helpGuide.setMaxWidth(1000);
        GridPane gridPane = new GridPane();
        ImageView first = new ImageView("Manual.png");
        gridPane = addImage(gridPane, helpGuide, first, 0);
        ImageView second = new ImageView("Extensions.png");
        gridPane = addImage(gridPane, helpGuide, second, 1);
        ImageView third = new ImageView("ColorPalette.png");
        gridPane = addImage(gridPane, helpGuide, third, 2);
        helpGuide.setContent(gridPane);
        helpGuide.setPrefSize(1100, 500);
        alert.getDialogPane().setContent(helpGuide);
        alert.showAndWait();
    }

    private GridPane addImage(GridPane gridPane, ScrollPane scrollPane, ImageView imageView, int order){
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(scrollPane.getMaxWidth());
        gridPane.setConstraints(imageView, 0, order);
        gridPane.getChildren().addAll(imageView);
        return gridPane;
    }
}
