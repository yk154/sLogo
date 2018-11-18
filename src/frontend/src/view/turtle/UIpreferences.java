package view.turtle;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.util.StringConverter;
import turtleModel.UserControlsListener;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UIpreferences
 *
 * Set of user preferable features on the UILog
 * user preferable features: pen color, turtle image, background color, pen thickness, pen up/down and turtle speed
 *
 * @author Amy Kim
 * @author Natalie
 */

public class UIpreferences implements ViewListener {
    private static final int ICON_SIZE = 60;
    private static final int SPACING = 3;

    private static final List<String> TURTLE_IMG_URLS = List.of(
            "default_turtle01.png",
            "default_turtle02.png",
            "default_turtle03.png",
            "default_turtle04.png"
    );
    public static final List<Image> TURTLE_IMAGES = Collections.unmodifiableList(
            TURTLE_IMG_URLS
                    .stream()
                    .map(url -> new Image(UIpreferences.class.getClassLoader().getResourceAsStream(url)))
                    .collect(Collectors.toList())
    );

    private VBox root;
    private ColorPicker myPen;
    private ColorPicker myPalette;
    private Slider penSlider;
    private Slider turtleSpeed;
    private Slider penSwitch;
    private Slider turtleRotation;
    private ComboBox<Integer> turtleChooser;
    private ComboBox<Integer> activeTurtleChooser;
    private List<UserControlsListener> userControlsListeners;

    public UIpreferences(){
        userControlsListeners = new ArrayList<>();
        activeTurtleChooser = new ComboBox<>();
        this.drawTurtle();
        this.drawSpeedSlider();
        this.drawPen();
        this.drawPalette();
        this.drawThickSlider();
        this.drawUpDown();
        this.drawRotationSlider();
        root = new VBox(SPACING);
        root.getStyleClass().add("icons-wrapper");
        root.getChildren().add(activeTurtleChooser);
        root.getChildren().add(turtleChooser);
        root.getChildren().add(turtleSpeed);
        root.getChildren().add(turtleRotation);
        root.getChildren().add(myPalette);
        root.getChildren().add(myPen);
        root.getChildren().add(penSlider);
        root.getChildren().add(penSwitch);
    }

    public void addUserControlListener(UserControlsListener u){
        userControlsListeners.add(u);
    }

    public void notifyAllUserControlListeners(){
        for (UserControlsListener u : userControlsListeners){
            System.out.println("penswitch value is " + getPenSwitch().getValue());
            u.hasChangedPreferences(activeTurtleChooser.getValue(), turtleSpeed.getValue() * 10,
                    turtleRotation.getValue(),
                    getPenSwitch().getValue());
        }
    }

    private void drawTurtle(){
        turtleChooser = new ComboBox<>();
        turtleChooser.setCellFactory(c -> new TurtleCell(0));
        turtleChooser.getItems().addAll(0, 1, 2, 3);
        turtleChooser.getSelectionModel().selectedItemProperty().addListener((e, o, n) -> {
            turtleChooser.setButtonCell(new TurtleCell(n));
        });
        turtleChooser.getSelectionModel().select(0);
        turtleChooser.getStyleClass().add("turtle-chooser");

        tooltip(turtleChooser, "Change your Turtle");
    }

    public void makeDropbar(ObservableList<Integer> turtleIDs){
        activeTurtleChooser.setItems(turtleIDs);
        activeTurtleChooser.setValue(turtleIDs.get(0));
        tooltip(activeTurtleChooser, "Active Turtle IDs");
    }

    private void drawPen(){
        myPen = new ColorPicker();
        myPen.getStyleClass().add("pen-wrapper");
        tooltip(myPen, "Pen Color");
    }

    private void drawRotationSlider(){
        turtleRotation = new Slider();
        turtleRotation.setMin(0);
        turtleRotation.setMax(360);
        turtleRotation.setValue(0);
        turtleRotation.getStyleClass().add("turtleRotation");
        turtleRotation.setOnMouseMoved(e -> notifyAllUserControlListeners());
        tooltip(turtleRotation, "Rotation of Turtle");
    }

    private void drawThickSlider(){
        penSlider = new Slider();
        penSlider.setMin(1);
        penSlider.setMax(10);
        penSlider.setValue(1);
        tooltip(penSlider, "Pen Thickness");
    }

    private void drawUpDown(){
        penSwitch = new Slider();
        sliderToswitch(penSwitch);
        penSwitch.setShowTickLabels(true);

        //convert integer to string
        penSwitch.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 1.0) return "Down";
                return "Up";
            }
            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Down":
                        return 1.0;
                    default:
                        return 0.0;
                }
            }
        });

        penSwitch.getStyleClass().add("switch");
        penSwitch.setOnMouseReleased(e -> notifyAllUserControlListeners());
        tooltip(penSwitch, "Pen Up/Down");
    }

    private void sliderToswitch(Slider s) {
        s.setMin(0);
        s.setMax(1);
        s.setValue(1);
        s.setMinorTickCount(0);
        s.setMajorTickUnit(1);
        s.setSnapToTicks(true);
        s.setShowTickMarks(true);
    }

    private void drawSpeedSlider() {
        turtleSpeed = new Slider();
        turtleSpeed.setMin(0);
        turtleSpeed.setMax(10);
        turtleSpeed.setValue(5);
        turtleSpeed.setOnMouseMoved(e -> notifyAllUserControlListeners());
        tooltip(turtleSpeed, "Turtle Speed");
    }

    private void drawPalette(){
        myPalette = new ColorPicker();
        myPalette.getStyleClass().add("palette-wrapper");
        tooltip(myPalette, "Background Color");
    }

    private void tooltip(Node target, String text) {
        var tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
        Tooltip.install(target, tooltip);
    }

    public ComboBox<Integer> getTurtleChooser() { return turtleChooser; }

    public VBox getMyicon() {
        return root;
    }

    public ColorPicker getMyPen() {
        return myPen;
    }

    public ColorPicker getMyPalette() {
        return myPalette;
    }

    public void updatePreferenceValues(double speed, double rotation, double isPenDown){
        turtleSpeed.setValue(speed / 10);
        double adjustedRotation = rotation % 360;
        if (adjustedRotation < 0) {
            adjustedRotation += 360;
        }
        turtleRotation.setValue(adjustedRotation);
        penSwitch.setValue(isPenDown);
    }

    public Slider getPenSlider(){ return penSlider; }

    public Slider getTurtleSpeed() { return turtleSpeed; } //Turtle Speed Slider

    public Slider getTurtleRotation() { return turtleRotation; } // Turtle Rotation

    public Slider getPenSwitch() { return penSwitch; } //Pen Up and Down Switch

    public ComboBox<Integer> getActiveTurtleChooser() { return activeTurtleChooser; }

    @Override
    public void viewHasChanged(int id, double speed, double rotation, double isPenUp) {
        if (id != activeTurtleChooser.getValue()) return;
        updatePreferenceValues(speed, rotation, isPenUp);
    }

    private class TurtleCell extends ListCell<Integer> {
        public TurtleCell(int idx) {
            var iv = new ImageView(TURTLE_IMAGES.get(idx));
            iv.setFitWidth(ICON_SIZE);
            iv.setFitHeight(ICON_SIZE);
            setGraphic(iv);
        }
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if(item != null) {
                var iv = new ImageView(TURTLE_IMAGES.get(item));
                iv.setFitWidth(ICON_SIZE);
                iv.setFitHeight(ICON_SIZE);
                setGraphic(iv);
            }
        }
    }
}
