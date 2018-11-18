package parsing;

import commands.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import turtleModel.TurtleModel;

import java.util.*;

/**
 * Calls the parsed commands on the Turtle
 * @author Natalie
 */
public class Interpreter implements ResponseListener, NumberOfActiveTurtlesListener {
    private Map<Integer, TurtleModel> turtleModelMap;
    private ObservableList<String> returnVals;
    private ObservableList<Integer> activeTurtleIDs;
    private ObservableList<String> userVariables;
    private List<ClearTrailsListener> clearTrailsListeners;
    private List<TurtleQueryListener> turtleQueryListeners;
    private List<SetToLocationListener> setToLocationListeners;
    private List<CreationListener> creationListeners;
    private List<HomeListener> homeListeners;

    private List<BackgroundChangeListener> backgroundChangeListeners;
    private List<PenColorChangeListener> penColorChangeListeners;
    private static Map<String, Double> customVariables;
    private static Map<String, CustomCommand> customCommands;

    public Interpreter(){
        returnVals = FXCollections.observableArrayList();
        activeTurtleIDs = FXCollections.observableArrayList();
        userVariables = FXCollections.observableArrayList();
        turtleModelMap = new HashMap<>();
        turtleModelMap.put(1, new TurtleModel(1));
        activeTurtleIDs.add(1);
        clearTrailsListeners = new ArrayList<>();
        turtleQueryListeners = new ArrayList<>();
        setToLocationListeners = new ArrayList<>();
        creationListeners = new ArrayList<>();
        homeListeners = new ArrayList<>();
        backgroundChangeListeners = new ArrayList<>();
        penColorChangeListeners = new ArrayList<>();

        this.customVariables = new HashMap<>();
        this.customCommands = new HashMap<>();
    }

    public TurtleModel getTurtleModel(int id){
        return turtleModelMap.get(id);
    }

    public void createNewTurtles(int numTurtles){
        int startingId = turtleModelMap.size() + 1;
        List<Integer> idsToCreate = new ArrayList<>();
        for (int i = 0; i < numTurtles; i++) {
            TurtleModel turtleModel = new TurtleModel(startingId);
            turtleModelMap.put(startingId, turtleModel);
            idsToCreate.add(startingId);
            startingId++;
        }
        activeTurtleIDs.addAll(idsToCreate);
        notifyAllCreationListeners(idsToCreate);
    }

    public void addClearTrailListener(ClearTrailsListener c){
        clearTrailsListeners.add(c);
    }

    public void addTurtleQueryListener(TurtleQueryListener t){
        turtleQueryListeners.add(t);
    }

    public void addSetToLocationListener(SetToLocationListener s){
        setToLocationListeners.add(s);
    }

    public void addCreationListener(CreationListener c){
        creationListeners.add(c);
    }

    public void addHomeListener(HomeListener h){
        homeListeners.add(h);
    }

    public void addBackgroundChangeListener(BackgroundChangeListener b) { backgroundChangeListeners.add(b); }

    public void addPencolorChangeListener(PenColorChangeListener p) { penColorChangeListeners.add(p); }

    public void notifyAllClearTrailsListeners(){
        for (ClearTrailsListener c : clearTrailsListeners){
            c.hasCleared();
        }
    }

    public void notifyAllTurtleQueryListeners(String query, int id){
        for (TurtleQueryListener t : turtleQueryListeners){
            t.hasQueried(query, id);
        }
    }

    public void notifyAllSetToLocationListeners(double xCoord, double yCoord, int id){
        for (SetToLocationListener s : setToLocationListeners){
            s.hasSetTo(xCoord, yCoord, id);
        }
    }

    private void notifyAllHomeListeners(){
        for (HomeListener h : homeListeners){
            h.hasGoneHome();
        }
    }

    private void notifyAllCreationListeners(List<Integer> turtleIDs){
        for (CreationListener c : creationListeners){
            c.hasAddedTurtles(turtleIDs);
        }
    }

    private void notifyAllBackgroundChangeListeners(int idx) {
        for (BackgroundChangeListener b : backgroundChangeListeners) {
            b.notifyBackgroundChange(idx);
        }
    }

    private void notifyAllPenColorChangeListener(int idx) {
        for (PenColorChangeListener p : penColorChangeListeners) {
            p.notifyPenColorChange(idx);
        }
    }

    public ObservableList<String> getReturnVals() {
        return returnVals;
    }

    public ObservableList<Integer> getActiveTurtleIDs() { return activeTurtleIDs; }

    public ObservableList<String> getUserVariables(){ return userVariables; }

    public void executeCommands(List<Command> cmdList, boolean isRepeatCommand){
        for (Command c : cmdList){
            if(c.getAction().equals("DoTimes")){
                DoTimesCommand loop = (DoTimesCommand) c;

                executeCommands(loop.getToExecute(), true);
            }
            if (c.getAction().equals("Turtles")) {
                returnVals.add(Double.toString(turtleModelMap.size()));
            }
            if(c.getAction().equals("For")){
                ForCommand loop = (ForCommand) c;

                executeCommands(loop.getToExecute(), true);
            }
            if (c.getAction().equals("Tell")){
                createNewTurtles((int) c.getResult());
                returnVals.add(Double.toString(c.getResult()));
            }
            if (c.getAction().equals("Repeat")) {
                RepeatCommand loop = (RepeatCommand) c;
                for (int i=0; i<loop.getNumIterations(); i++) {
                    executeCommands(loop.getToExecute(), true);
                }
            }
            if (c.getAction().equals("MakeUserInstruction")){
                CustomCommand com = (CustomCommand) c;
                customCommands.put(com.getCommandName(), com);
                userVariables.add(com.getCommandName() + " = " + com);
            }
            if (c.getAction().equals("Home")){
                notifyAllHomeListeners();
            } if (c.getAction().equals("ClearScreen")){
                notifyAllHomeListeners();
                notifyAllClearTrailsListeners(); // TODO: Fix concurrent modification exception
            } if (c.getAction().equals("MakeVariable")){
                CustomVariable var = (CustomVariable) c;
                customVariables.put(var.getVariableName(), var.getVariableValue());
                userVariables.add(var.getVariableName() + " = " + var.getVariableValue());
            }
            for (int id : activeTurtleIDs){
                if (c.getAction().equals("Right")){
                    //createNewTurtles((int) c.getResult()); // TODO: Change back (only using while waiting for backend)
                    returnVals.add(Double.toString(turtleModelMap.get(id).right(c.getResult())));
                } if (c.getAction().equals("Forward")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).forward(c.getResult(), isRepeatCommand)));
                } if (c.getAction().equals("Backward")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).back(c.getResult(), isRepeatCommand)));
                } if (c.getAction().equals("Left")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).left(c.getResult())));
                } if (c.getAction().equals("SetHeading")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).setHeading(c.getResult())));
                } if (c.getAction().equals("HideTurtle")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).hide()));
                } if (c.getAction().equals("ShowTurtle")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).show()));
                } if (c.getAction().equals("PenUp")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).penUp()));
                } if (c.getAction().equals("PenDown")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).penDown()));
                } if (c.getAction().equals("Heading")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).getRotation()));
                } if (c.getAction().equals("XCoordinate")){
                    notifyAllTurtleQueryListeners(c.getAction(), id);
                } if (c.getAction().equals("YCoordinate")){
                    notifyAllTurtleQueryListeners(c.getAction(), id);
                } if (c.getAction().equals("IsPenDown")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).getIsPenDown()));
                } if (c.getAction().equals("IsShowing")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).getIsHiding()));
                } if (c.getAction().equals("SetTowards")){
                    returnVals.add(Double.toString(turtleModelMap.get(id).setTowards(c.getParam1(), c.getParam2())));
                } if (c.getAction().equals("SetPosition")){
                    notifyAllSetToLocationListeners(c.getParam1(), c.getParam2(), id);
                } if (c.getAction().equals("SetBackground")) {
                    notifyAllBackgroundChangeListeners((int) c.getParam1());
                } if (c.getAction().equals("SetPenColor")) {
                    notifyAllPenColorChangeListener((int) c.getParam1());
                }
            }
        }
    }

    public  void addCustomVariable(String s, Double d){
        customVariables.put(s,d);
    }

    public  void addCustomCommand(String s, CustomCommand c){
        customCommands.put(s,c);
    }

    public Map<String, Double> getCustomVariables(){
        return customVariables;
    }

    public Map<String, CustomCommand> getCustomCommands(){
        return customCommands;
    }

    @Override
    public void receivedResponse(double response) {
        returnVals.add(Double.toString(response));
    }

    @Override
    public void hasChangedActivation(int id, boolean isActive) {
        System.out.println("turtle of id " + id + "has inactive of " + isActive);
        if (isActive && !activeTurtleIDs.contains(id)){
            activeTurtleIDs.add(id);
        } else if (!isActive && activeTurtleIDs.contains(id)) {
            activeTurtleIDs.removeIf((Integer activeID) -> activeID == id);
        }
    }
}
