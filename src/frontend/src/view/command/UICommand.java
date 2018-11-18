package view.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * UICommand
 *
 * Set of text boxes for Input command, variables/commands log and Error Console on the UILog
 *
 * @author Amy Kim
 */

public class UICommand {
    private TextArea putCommand;
    private ListView viewReturned;
    private ListView viewVar;
    private TextField errorConsole;
    private VBox myBox;

    public UICommand(){
        myBox = new VBox(5);
        myBox.getStyleClass().add("console-wrapper");
        this.makeTextfield();
        this.makeErrorConsole();
        viewReturned = new ListView<>();
        viewVar = new ListView();
        myBox.getChildren().add(putCommand);
        myBox.getChildren().add(errorConsole);
    }

    /**
     * making textfield so that the user can write one line of command.
     */
    private void makeTextfield(){
        putCommand = new TextArea();
        putCommand.getStyleClass().add("inputCommand-wrapper");
        Tooltip tooltip = new Tooltip("Type Your Command Here");
        tooltipDelay(tooltip);
        Tooltip.install(putCommand, tooltip);
    }

    public void makeReturned(ObservableList<String> returnVals){
        viewReturned.setItems(returnVals);
        viewReturned.getStyleClass().add("viewReturned-wrapper");
        Tooltip tooltip = new Tooltip("Returned Values");
        tooltipDelay(tooltip);
        Tooltip.install(viewReturned, tooltip);
    }

    public void makeVar(ObservableList<String> viewVals){
        viewVar.setItems(viewVals);
        viewVar.getStyleClass().add("listview-wrapper");
        Tooltip tooltip = new Tooltip("User Defined Variables/Commands");
        tooltipDelay(tooltip);
        Tooltip.install(viewVar, tooltip);
    }

    private void makeErrorConsole() {
        errorConsole = new TextField();
        errorConsole.setEditable(false);
        errorConsole.getStyleClass().add("error-wrapper");
        Tooltip tooltip = new Tooltip("Error Console");
        tooltipDelay(tooltip);
        Tooltip.install(errorConsole, tooltip);
    }

    private void tooltipDelay(Tooltip tooltip) {
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
    }

    public TextArea getPutCommand(){
        return putCommand;
    }

    public ListView getViewBox() { return viewReturned; }

    public ListView getVarBox() { return viewVar; }

    public VBox getMyBox() { return myBox; }

    public TextField getErrorConsole() { return errorConsole; }

}

