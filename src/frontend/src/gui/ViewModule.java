package gui;

import control.ControlSetup;
import control.Controller;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import view.UIsetup;

import java.util.NoSuchElementException;

/**
 * ViewModule Class
 *
 * Wrapped all the UILog features and Controller by TabPane.
 *
 * mySlogoUI - All the features on the UILog
 * mySlogoControl - Control the features
 *
 * @author Amy Kim
 */

public class ViewModule {
    private TabPane myTabs;
    private boolean isMyFirstTab;

    public ViewModule() {
        isMyFirstTab = true;
        myTabs = new TabPane();
        setupTab();
        setTabPolicy();
        myTabs.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
    }

    /**
     * If clicked log button, give new tab which contains log list
     * @param logs
     */
    private void makeALogTab(ObservableList<String> logs) {
        var tab = new Tab(" Log");
        var pane = new Pane();
        pane.getChildren().addAll(new ListView<>(logs));
        tab.setContent(pane);
        myTabs.getTabs().add(tab);
        myTabs.getSelectionModel().select(myTabs.getTabs().size()-1);
    }

    public void makeATab() {
        var mySlogoUI = new UIsetup();
        var mySlogoControl = new ControlSetup(mySlogoUI);
        new Controller(mySlogoControl, this::makeALogTab);
        try {
            var tab = namedTab();
            tab.setContent(mySlogoUI.getRoot());
            if (isMyFirstTab) {
                tab.setClosable(false);
                isMyFirstTab = false;
            }
            myTabs.getTabs().add(tab);
        } catch (Exception e) {} //Stay the page if no additional pages
    }

    public void setupTab() {
        var addTab = new Tab("");
        var plusButton = new Button("+");
        plusButton.getStyleClass().add("plusBtn");
        addTab.setGraphic(plusButton);
        addTab.setClosable(false);
        plusButton.setOnAction(e -> {
            makeATab();
            myTabs.getSelectionModel().select(myTabs.getTabs().size()-1);
        });
        addTab.selectedProperty().addListener((e, o, n) -> {
            myTabs.getSelectionModel().select(1);
        });
        myTabs.getTabs().add(addTab);
        makeATab();
        myTabs.getSelectionModel().select(myTabs.getTabs().size()-1);
    }

    public void setTabPolicy() {
        myTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        myTabs.getTabs().addListener((ListChangeListener<Tab>) c -> {
            while(c.next()) {
                if(c.wasRemoved()) {
                    if (c.getList().size() == 0) makeATab();
                }
            }
        });
    }

    public Tab namedTab() throws NoSuchElementException {
        if(isMyFirstTab) {
            return new Tab("Turtle");
        }
        else {
            var dialog = new TextInputDialog("Turtle");
            dialog.getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
            dialog.setTitle("WELCOME TO SLOGO!");
            dialog.setHeaderText("Start your SLOGO!");
            dialog.setContentText("Set Your Tab Name:");
            var res = dialog.showAndWait();
            return new Tab(res.get());
        }
    }

    public TabPane getMyTab() { return myTabs; }
}
