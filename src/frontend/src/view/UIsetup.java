package view;

import javafx.scene.layout.*;
import view.command.*;
import view.turtle.*;

import java.util.function.Consumer;

/**
 * UISetup
 *
 * View all the features on the UILog
 *  - creates a scene for UILog, adds other UILog elements and return them so that Control SetUp can control the features
 *
 * @author Amy Kim
 */

public class UIsetup {
    public static final String LANGUAGE_RESOURCE_PACKAGE = "";
    public static final int COL1 = 605;
    public static final int COL2 = 90;
    public static final int COL3 = 385;
    public static final int COL4 = 140;

    public static final int ROW1 = 100;
    public static final int ROW2 = 360;
    public static final int ROW3 = 190;
    public static final int ROW4 = 70;

    private GridPane myRoot;
    private VBox mylistBox;
    private VBox myVarbox;
    private VBox mybtnbox;
    private UIchoice settings;
    private UIpaper myPaper;
    private UIpreferences myPrefer;
    private UIbuttons myButtons;
    private UICommand myTextInput;

    public UIsetup() {
        myRoot = new GridPane();
        myRoot.getStyleClass().add("grid-wrapper");

        sceneColumn();
        sceneRow();

        mylistBox = new VBox();
        myVarbox = new VBox();
        mybtnbox = new VBox();
        mylistBox.getStyleClass().add("listbox-wrapper");
        myVarbox.getStyleClass().add("varbox-wrapper");
        mybtnbox.getStyleClass().add("btnbox-wrapper");

        settings = new UIchoice();
        myPaper = new UIpaper();
        myPrefer = new UIpreferences();
        myTextInput = new UICommand();
        myButtons = new UIbuttons(myTextInput, myPaper);
        mylistBox.getChildren().add(myTextInput.getViewBox());
        myVarbox.getChildren().add(myTextInput.getVarBox());
        mybtnbox.getChildren().add(myButtons.getMybuttons());

        addToRoot();
    }

    private void addToRoot(){
        myRoot.add(myPaper.getMyPaper(), 0, 0, 3, 2);
        myRoot.add(settings.getMyDropbar(), 3, 0);
        myRoot.add(myPrefer.getMyicon(), 3,1);
        myRoot.add(myTextInput.getMyBox(), 0, 2, 1, 2);
        myRoot.add(myVarbox,1,2,3,1);
        myRoot.add(mylistBox,2,3 , 3, 3);
        myRoot.add(mybtnbox, 1, 3);
    }


    private void sceneColumn() {
        myRoot.getColumnConstraints().add(new ColumnConstraints(COL1));
        myRoot.getColumnConstraints().add(new ColumnConstraints(COL2));
        myRoot.getColumnConstraints().add(new ColumnConstraints(COL3));
        myRoot.getColumnConstraints().add(new ColumnConstraints(COL4));
    }

    private void sceneRow() {
        myRoot.getRowConstraints().add(new RowConstraints(ROW1));
        myRoot.getRowConstraints().add(new RowConstraints(ROW2));
        myRoot.getRowConstraints().add(new RowConstraints(ROW3));
        myRoot.getRowConstraints().add(new RowConstraints(ROW4));
    }

    public GridPane getRoot(){
        return myRoot;
    }

    public UIpaper getMyPaper() {
        return myPaper;
    }

    public UIpreferences getMyPrefer() {
        return myPrefer;
    }

    public UIbuttons getMyButtons() {
        return myButtons;
    }

    public UICommand getMyTextInput() { return myTextInput;}

    public UIchoice getSettings() { return settings; }
}