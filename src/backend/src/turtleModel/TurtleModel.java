package turtleModel;

import parsing.ActivateListener;
import parsing.HomeListener;
import parsing.NumberOfActiveTurtlesListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Backend component of the Turtle, communicates relative movements to the front-end
 * @author Natalie
 */
public class TurtleModel implements ActivateListener, UserControlsListener {

    private int id;
    private double rotation;
    private double xMovement;
    private double yMovement;
    private double distanceTraveled;
    private double currentScale;
    private double speed;
    private boolean isPenDown;
    private boolean isHiding;
    private boolean isActive;
    private List<LocationListener> locationListeners;
    private List<RotationListener> rotationListeners;
    private List<VisibilityListener> visibilityListeners;
    private List<NumberOfActiveTurtlesListener> numberOfActiveTurtlesListeners;
    private List<SpeedListener> speedListeners;
    private List<PenListener> penListeners;

    public TurtleModel(int id) {
        this.id = id;
        this.isActive = true;
        this.speed = 50;
        rotation = 0;
        xMovement = 0;
        yMovement = 0;
        distanceTraveled = 0;
        isPenDown = true;
        isHiding = false;
        currentScale = 1;
        locationListeners = new ArrayList<>();
        rotationListeners = new ArrayList<>();
        visibilityListeners = new ArrayList<>();
        numberOfActiveTurtlesListeners = new ArrayList<>();
        speedListeners = new ArrayList<>();
        penListeners = new ArrayList<>();
    }

    public double forward(double pixels, boolean isRepeat) {
        xMovement = (-1) * (Math.round(pixels * Math.cos(Math.toRadians(rotation))));
        yMovement = (-1) * (Math.round(pixels * Math.sin(Math.toRadians(rotation))));
        notifyAllLocationListeners(isRepeat);
        return pixels;
    }

    public double back(double pixels, boolean isRepeat) {
        xMovement = Math.round(pixels * Math.cos(Math.toRadians(rotation)));
        yMovement = Math.round(pixels * Math.sin(Math.toRadians(rotation)));
        notifyAllLocationListeners(isRepeat);
        return pixels;
    }

    public double right(double degrees) {
        rotation += degrees;
        notifyAllRotationListeners();
        return degrees;
    }

    public double left(double degrees) {
        rotation -= degrees;
        notifyAllRotationListeners();
        return degrees;
    }

    public double setHeading(double degrees) {
        double currentRotation = rotation % 360;
        double lesserRotation = Math.abs(degrees - currentRotation) <= Math.abs((360 - currentRotation) + degrees) ?
                Math.abs(degrees - currentRotation) : Math.abs((360 - currentRotation) + degrees);
        rotation = degrees;
        notifyAllRotationListeners();
        return lesserRotation;
    }

    public int hide() {
        isHiding = true;
        notifyAllVisibilityListeners();
        return getIsHiding();
    }

    public int show() {
        isHiding = false;
        notifyAllVisibilityListeners();
        return getIsHiding();
    }

    public int penDown() {
        isPenDown = true;
        notifyAllPenListeners();
        return getIsPenDown();
    }

    public int penUp() {
        isPenDown = false;
        notifyAllPenListeners();
        return getIsPenDown();
    }

    public int getIsPenDown() {
        if (isPenDown){
            return 1;
        } else {
            return 0;
        }
    }

    public int getIsHiding() {
        if (isHiding){
            return 0;
        } else {
            return 1;
        }
    }

    public double setTowards(double xCoord, double yCoord){
        double degrees = Math.toDegrees(Math.atan2(-1 * yCoord, xCoord)) + 180; // turtle is initialized facing left
        double degreesMoved = degrees - rotation;
        rotation = degrees;
        notifyAllRotationListeners();
        return degreesMoved;
    }

    public double getRotation(){
        return rotation;
    }

    public void addLocationListener(LocationListener l){
        locationListeners.add(l);
    }

    public void addRotationListener(RotationListener r){
        rotationListeners.add(r);
    }

    public void addVisibilityListener(VisibilityListener v){
        visibilityListeners.add(v);
    }

    public void addNumberOfActiveTurtlesListener(NumberOfActiveTurtlesListener n){
        numberOfActiveTurtlesListeners.add(n);
    }

    public void addSpeedListener(SpeedListener s){
        speedListeners.add(s);
    }

    public void addPenListener(PenListener p){
        penListeners.add(p);
    }

    private void notifyAllLocationListeners(boolean isRepeat){
        if (locationListeners.isEmpty()) System.out.println("it's empty");
        for (LocationListener l : locationListeners){
            l.hasMoved(xMovement, yMovement, isRepeat);
        }
    }

    private void notifyAllVisibilityListeners(){
        for (VisibilityListener v : visibilityListeners){
            v.hasChangedVisibility(isHiding);
        }
    }

    private void notifyAllRotationListeners(){
        for (RotationListener r : rotationListeners){
            r.hasRotated(rotation);
        }
    }

    private void notifyAllNumberOfActiveTurtlesListeners(){
        for (NumberOfActiveTurtlesListener n : numberOfActiveTurtlesListeners){
            n.hasChangedActivation(id, isActive);
        }
    }

    private void notifyAllPenListeners(){
        for (PenListener p : penListeners){
            p.hasChanged(isPenDown);
        }
    }

    private void notifyAllSpeedListeners(){
        for (SpeedListener s : speedListeners){
            s.hasAdjustedSpeed(speed);
        }
    }

    public boolean getIsActive(){
        return isActive;
    }

    @Override
    public void hasSetActivate(boolean isActive) {
        this.isActive = isActive;
        notifyAllNumberOfActiveTurtlesListeners();
    }

    @Override
    public void hasChangedPreferences(int id, double speed, double rotation, double isPenDown) {
        if (this.id != id) return;
        this.speed = speed;
        this.rotation = rotation;
        this.isPenDown = (isPenDown == 1);
        notifyAllSpeedListeners();
        notifyAllRotationListeners();
        notifyAllPenListeners();
    }
}
