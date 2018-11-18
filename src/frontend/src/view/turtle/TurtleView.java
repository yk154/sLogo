package view.turtle;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import parsing.*;
import turtleModel.*;
import view.UIsetup;

/**
 * TurtleView
 *
 * Front end component of Turtle
 * @author Natalie Le (nl121)
 * @author Amy Kim(yk154)
 */

public class TurtleView implements LocationListener, RotationListener, VisibilityListener, TurtleQueryListener,
        HomeListener, PenListener, SetToLocationListener, ClearTrailsListener, SpeedListener {
    public static final int ICON_SIZE = 60;
    private final Image DEFAULT_TURTLE_IMG =
            new Image(TurtleView.class.getClassLoader().getResourceAsStream("default_turtle01.png"));
    private static final int SCREEN_CX = (UIsetup.COL1 + UIsetup.COL2 + UIsetup.COL3) / 2;
    private static final int SCREEN_CY = (UIsetup.ROW1 + UIsetup.ROW2) / 2;
    private final int FRAMES_PER_SECOND = 100;
    private final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    private final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    private List<IconLocationListener> iconLocationListeners;
    private List<ResponseListener> responseListeners;
    private List<ActivateListener> activateListeners;
    private List<ViewListener> viewListeners;
    private List<Coordinate> locationLog;
    private Coordinate initialPosition;
    private double currentScale;
    private boolean isPenDown;
    private double xOffset;
    private double yOffset;
    private double speed;
    private ImageView myIcon;
    private boolean isActive;
    private int id;
    private boolean isHiding;
    private Coordinate destination;
    private Timeline animationTimeline;
    private Point2D iconVelocity;
    private Tooltip tooltip;

    public TurtleView(int id) {
        this.id = id;
        this.isActive = true;
        isHiding = false;
        this.speed = 50;
        var turtle = DEFAULT_TURTLE_IMG;
        myIcon = new ImageView(turtle);
        myIcon.setFitWidth(ICON_SIZE);
        myIcon.setFitHeight(ICON_SIZE);
        myIcon.getStyleClass().add("turtle-wrapper");
        addIconEventHandlers();
        myIcon.setX(SCREEN_CX - TurtleView.ICON_SIZE / 2);
        myIcon.setY(SCREEN_CY - TurtleView.ICON_SIZE / 2);
        isPenDown = true;
        iconLocationListeners = new ArrayList<>();
        responseListeners = new ArrayList<>();
        activateListeners = new ArrayList<>();
        viewListeners = new ArrayList<>();
        locationLog = new ArrayList<>();
        resetTrailLog();
        initialPosition = new Coordinate(myIcon.getX(), myIcon.getY());
        currentScale = 1;
        destination = new Coordinate(myIcon.getX(), myIcon.getY());
        animationTimeline = new Timeline();
        iconVelocity = new Point2D(0, 0);

        var animationFrame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> animate(destination, SECOND_DELAY));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.getKeyFrames().add(animationFrame);

        setVisibility(isHiding);
        displayIsActive();
        tooltip = new Tooltip(fetchPosition());
        Tooltip.install(myIcon, tooltip);
        tooltip.addEventFilter(MouseEvent.ANY, E -> {
            tooltip.getScene();
            E.consume();
        });
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
    }

    private String fetchPosition() {
        return "x: " + getModelX() + ",\n" +
                "y: " + getModelY() + ",\n" +
                "rotation: "  + getModelRot() + "°\n" +
                "id: " + id;
    }

    public int getNumericPenStatus(){
        if (isPenDown) return 1;
        return 0;
    }

    private void animate(Coordinate destination, double elapsedTime){
        if (myIcon.getX() < 0 || myIcon.getX() > SCREEN_CX * 2 - TurtleView.ICON_SIZE ||
                myIcon.getY() < 0 || myIcon.getY() > SCREEN_CY * 2 - TurtleView.ICON_SIZE) { // outside paper
            myIcon.setVisible(false); // does not change isHiding
        } else {
            setVisibility(isHiding);
        }
        if (Math.abs(myIcon.getX() - destination.getX()) < 1 && Math.abs(myIcon.getY() - destination.getY()) < 1){
            animationTimeline.stop();
            updateLocationLog();
            tooltip.setText(fetchPosition());
            notifyIconLocationListeners();
            return;
        }
        myIcon.setX(myIcon.getX() + iconVelocity.getX() * elapsedTime);
        myIcon.setY(myIcon.getY() + iconVelocity.getY() * elapsedTime);
    }

    private void addIconEventHandlers(){
        myIcon.addEventHandler(MouseDragEvent.MOUSE_PRESSED, event -> setDragOffsets(event));
        myIcon.addEventHandler(MouseDragEvent.MOUSE_RELEASED, event -> releaseAtLocation(event));
        myIcon.setOnMouseClicked(event -> {
            switchIsActive();
            displayIsActive();
        });
    }

    private void switchIsActive(){
        isActive = !isActive;
        displayIsActive();
        notifyAllActivateListeners();
    }

    private void displayIsActive(){
        if (!isActive){
            ColorAdjust grayScale = new ColorAdjust();
            grayScale.setSaturation(-1);
            myIcon.setEffect(grayScale);
        } else {
            myIcon.setEffect(null);
        }
    }

    public ImageView getIcon() {
        return myIcon;
    }

    public boolean getIsPenDown(){
        return isPenDown;
    }

    private void move(double xMovement, double yMovement, boolean isRepeat) {
        if (isRepeat){ // no animations for for loops
            myIcon.setX(myIcon.getX() + xMovement);
            myIcon.setY(myIcon.getY() + yMovement);
            updateLocationLog();
            tooltip.setText(fetchPosition());
            notifyIconLocationListeners();
        } else {
            destination = new Coordinate(myIcon.getX() + xMovement, myIcon.getY() + yMovement);
            double distance = getDistanceBetween(new Coordinate(myIcon.getX(), myIcon.getY()), destination);
            iconVelocity = new Point2D(speed * (xMovement / distance) , speed * (yMovement / distance)); // unit vector
            animationTimeline.play();
        }
    }

    private void rotate(double rotation) {
        myIcon.setRotate(rotation % 360);
        notifyIconLocationListeners();
        notifyAllViewListeners();
    }

    private void setVisibility(boolean isHiding){
        myIcon.setVisible(!isHiding);
    }

    public double getModelX() { //GET Model TurtleView's pos X, used for tooltip
        return myIcon.getX() + ICON_SIZE/2 - SCREEN_CX;
    }

    public double getModelY() {
        return -(myIcon.getY() + ICON_SIZE/2 - SCREEN_CY);
    }

    public double getSpeed() { return speed; }

    public double getModelRot() { return myIcon.getRotate(); }

    private void setDragOffsets(MouseEvent event){
        xOffset = event.getX() - myIcon.getX();
        yOffset = event.getY() - myIcon.getY();
    }

    private void releaseAtLocation(MouseEvent event){
        Coordinate initialPosition = new Coordinate(myIcon.getX(), myIcon.getY());
        myIcon.setX(event.getX() - xOffset);
        myIcon.setY(event.getY() - yOffset);
        Coordinate endPosition = new Coordinate(myIcon.getX(), myIcon.getY());
        notifyResponseListeners(getDistanceBetween(initialPosition, endPosition));
        updateLocationLog();
        notifyIconLocationListeners();
    }

    private void goHome() {
        myIcon.setX(initialPosition.getX());
        myIcon.setY(initialPosition.getY());
        setVisibility(isHiding);
        double distance = getDistanceBetween(locationLog.get(locationLog.size() - 1), initialPosition);
        updateLocationLog();
        notifyIconLocationListeners();
        notifyResponseListeners(distance);
    }

    private void resetTrailLog(){
        locationLog.clear();
        updateLocationLog();
    }

    public void addIconLocationListener(IconLocationListener i) {
        iconLocationListeners.add(i);
    }

    public void addResponseListener(ResponseListener r){
        responseListeners.add(r);
    }

    public void addActivateListener(ActivateListener a){
        activateListeners.add(a);
    }

    public void addViewListener(ViewListener v){
        viewListeners.add(v);
    }

    private void notifyIconLocationListeners() {
        for (IconLocationListener i : iconLocationListeners) {
            i.hasMoved(locationLog, id);
            i.hasSpun(myIcon.getRotate(), id);
        }
    }

    private void notifyResponseListeners(double response) {
        for (ResponseListener r : responseListeners) {
            r.receivedResponse(response);
        }
    }

    private void notifyAllActivateListeners(){
        for (ActivateListener a : activateListeners){
            a.hasSetActivate(isActive);
        }
    }

    private void notifyAllViewListeners(){
        for (ViewListener v : viewListeners){
            v.viewHasChanged(id, speed, getModelRot(), getNumericPenStatus());
        }
    }

    private double getXCor(){
        double coordinate = myIcon.getX() - initialPosition.getX();
        if (coordinate < 0) {
            return Math.floor(coordinate);
        } else {
            return Math.ceil(coordinate);
        }
    }

    private double getYCor(){
        double coordinate = (-1) * (myIcon.getY() - initialPosition.getY());
        if (coordinate < 0) {
            return Math.floor(coordinate);
        } else {
            return Math.ceil(coordinate);
        }
    }

    public void clear(){
        resetTrailLog();
    }

    private void updateLocationLog(){
        locationLog.add(new Coordinate(myIcon.getX(), myIcon.getY()));
    }

    private double getDistanceBetween(Coordinate start, Coordinate end){
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    }

    @Override
    public void hasMoved(double xMovement, double yMovement, boolean isRepeat) { // notified from TurtleModel
        move(xMovement, yMovement, isRepeat);
    }

    @Override
    public void hasRotated(double rotation) { // notified from TurtleModel
        rotate(rotation);
        tooltip.setText(fetchPosition());
    }

    @Override
    public void hasChangedVisibility(boolean isHiding) {
        this.isHiding = isHiding;
        setVisibility(this.isHiding);
    }

    @Override
    public void hasQueried(String query, int id) {
        if (id != this.id) return;
        if (query.equals("XCoordinate")){
            notifyResponseListeners(getXCor());
        } else if (query.equals("YCoordinate")){
            notifyResponseListeners(getYCor());
        }
    }

    @Override
    public void hasGoneHome() {
        goHome();
    }

    @Override
    public void hasChanged(boolean isPenDown) {
        this.isPenDown = isPenDown;
        notifyAllViewListeners();
    }

    @Override
    public void hasSetTo(double xCoord, double yCoord, int id) {
        if (id != this.id) return;
        myIcon.setX(xCoord + initialPosition.getX());
        myIcon.setY((-1) * yCoord + initialPosition.getY());
        double distance = getDistanceBetween(locationLog.get(locationLog.size() - 1), new Coordinate(myIcon.getX(), myIcon.getY()));
        updateLocationLog();
        notifyIconLocationListeners();
        notifyResponseListeners(distance);
    }

    @Override
    public void hasCleared() {
        clear();
    }

    @Override
    public void hasAdjustedSpeed(double speed) {
        this.speed = speed;
    }
}