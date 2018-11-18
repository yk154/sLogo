package view.command;

import javafx.scene.control.Alert;
import view.turtle.UIpaper;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;


/**
 * UIbuttons
 *
 * Set of buttons on the UILog
 *
 * @author Amy Kim
 */

public class UIbuttons {

    private HBox root;
    private Button help;
    private UICommand uiCommand;
    private UIpaper myPaper;

    public UIbuttons(UICommand uiCommand, UIpaper paper){
        this.uiCommand = uiCommand;
        this.myPaper = paper;
        this.makeHelp();

        root = new HBox(20);
        root.getStyleClass().add("buttons-wrapper");
        root.getChildren().add(help);
    }


    private void makeHelp(){
        help = new Button("HELP");
        help.getStyleClass().add("help-wrapper");
    }

    public HBox getMybuttons() {
        return root;
    }

    public Button getHelp() { return help; }


}