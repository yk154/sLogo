package view.turtle;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import parsing.BackgroundChangeListener;
import parsing.ClearTrailsListener;
import utils.StyleUtils;
import view.UIsetup;

import javax.swing.text.Style;
import java.util.*;
import static utils.StyleUtils.*;

/**
 * UIpaper
 *
 * Set of paper grid on the UILog
 *
 * @author Natalie Le
 * @author Amy Kim
 */

public class UIpaper implements IconLocationListener, ClearTrailsListener {
    private static final int SCREEN_X = (UIsetup.COL1 + UIsetup.COL2 + UIsetup.COL3);
    private static final int SCREEN_Y = (UIsetup.ROW1 + UIsetup.ROW2);
    private Pane myPaper;
    private Map<Integer, TurtleView> myTurtleViewMap;
    private Set<Line> turtleTrails;
    private Color myColor;
    private double myStrokeWidth;

    public UIpaper(){
        this.makePaper();
        myColor = Color.BLACK;
        myStrokeWidth = 1;
    }

    private void makePaper(){
        myPaper = new Pane();
        myTurtleViewMap = new HashMap<>();
        myPaper.getStyleClass().add("myPaper-wrapper");
        addTurtleView(1);
        turtleTrails = new HashSet<>();
    }

    public void addTurtleView(int id) {
        TurtleView turtleView = new TurtleView(id);
        myTurtleViewMap.put(id, turtleView);
        myPaper.getChildren().add(turtleView.getIcon());
        turtleView.addIconLocationListener(this);
    }

    public Pane getMyPaper() {
        return myPaper;
    }

    public void setMyColor(Color color) {
        myColor = color;
    }

    public void setMyStrokeWidth(double width) {
        myStrokeWidth = width;
    }


    public void clearTrails(){
        for (Line trail : turtleTrails){
            myPaper.getChildren().remove(trail);
        }
    }

    public TurtleView getMyTurtleView(int id) {
        return myTurtleViewMap.get(id);
    }

    @Override
    public void hasCleared() {
        clearTrails();
    }

    @Override
    public void hasMoved(List<Coordinate> locationLog, int id) {
        if (!myTurtleViewMap.get(id).getIsPenDown() || locationLog.size() < 2) return; // pen is up
        Coordinate trailStart = locationLog.get(locationLog.size() - 2);
        Coordinate trailEnd = locationLog.get(locationLog.size() - 1);
        System.out.println(String.format("trail start is at %f, %f and trail end is at %f, %f", trailStart.getX(),
                trailStart.getY(), trailEnd.getX(), trailEnd.getY()));
        Line trail = new Line(trailStart.getX() + TurtleView.ICON_SIZE/2, trailStart.getY() + TurtleView.ICON_SIZE/2,
                trailEnd.getX() + TurtleView.ICON_SIZE/2, trailEnd.getY() + TurtleView.ICON_SIZE/2);
        trail.setStroke(myColor);
        trail.setStrokeWidth(myStrokeWidth);
        turtleTrails.add(trail);
        myPaper.getChildren().add(trail);
    }

    @Override
    public void hasSpun(double newAngle, int id) { }
}