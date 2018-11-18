package control;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import parsing.CmdParser;
import parsing.CreationListener;
import parsing.Interpreter;
import turtleModel.TurtleModel;
import view.turtle.TurtleView;

import java.util.List;
import java.util.function.Consumer;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Natalie
 * @author Amy
 */

public class Controller implements CreationListener {
    private ControlSetup controlPackage;
    private CmdParser parser;
    private Interpreter interpreter;
    private Consumer<ObservableList<String>> logger;
    private ObservableList<String> logs;

    public Controller(ControlSetup controlPackage, Consumer<ObservableList<String>> logger){
        this.logger = logger;
        this.controlPackage = controlPackage;
        parser = new CmdParser();
        interpreter = parser.getInterpreter();
        interpreter.addCreationListener(this);
        interpreter.addBackgroundChangeListener(controlPackage.getControlPreferences());
        interpreter.addPencolorChangeListener(controlPackage.getControlPreferences());
        controlPackage.getUIPackage().getSettings().registerListener(parser::setLanguage);
        //controlPackage.getUIPackage().getMyTextInput().getVarBox()
        //myErrors = ResourceBundle.getBundle("resources/Errors.properties");
        logSendHandler();
        setupNewTurtle(1);
        controlPackage.getUIPackage().getMyTextInput().makeReturned(interpreter.getReturnVals());
        controlPackage.getUIPackage().getMyPrefer().makeDropbar(interpreter.getActiveTurtleIDs());
        controlPackage.getUIPackage().getMyTextInput().makeVar(interpreter.getUserVariables());
        interpreter.addClearTrailListener(controlPackage.getUIPackage().getMyPaper());
        controlPackage.getUIPackage().getMyPrefer().getActiveTurtleChooser().setOnAction(this::myActiveIDChooserHandler);
        sendInputHandler();
    }

    public void setupNewTurtle(int id){
        TurtleModel turtleModel = interpreter.getTurtleModel(id);
        turtleModel.addRotationListener(controlPackage.getTurtleView(id));
        turtleModel.addLocationListener(controlPackage.getTurtleView(id));
        turtleModel.addVisibilityListener(controlPackage.getTurtleView(id));
        turtleModel.addPenListener(controlPackage.getTurtleView(id));
        turtleModel.addNumberOfActiveTurtlesListener(interpreter);
        turtleModel.addSpeedListener(controlPackage.getTurtleView(id));

        controlPackage.getTurtleView(id).addActivateListener(turtleModel);
        controlPackage.getTurtleView(id).addViewListener(controlPackage.getUIPackage().getMyPrefer());
        controlPackage.getUIPackage().getMyPrefer().addUserControlListener(turtleModel);

        interpreter.addTurtleQueryListener(controlPackage.getTurtleView(id));
        interpreter.addSetToLocationListener(controlPackage.getTurtleView(id));
        interpreter.addHomeListener(controlPackage.getTurtleView(id));
        interpreter.addClearTrailListener(controlPackage.getTurtleView(id));
        controlPackage.getTurtleView(id).addResponseListener(interpreter);
    }

    private void logSendHandler() {
        logs = observableArrayList();
        controlPackage.getUIPackage().getSettings().getSceneBtn().setOnMouseClicked(e -> {
            logger.accept(logs);
        });
    }

    private void sendInputHandler() {
        controlPackage.getUIPackage().getMyTextInput().getPutCommand().setOnKeyPressed(event -> { //When pressed Enter
            if(event.getCode() == KeyCode.ENTER) {
                var area = controlPackage.getUIPackage().getMyTextInput().getPutCommand();
                if(event.isShiftDown()) {
                    area.appendText("\n");
                }else {
                    try {
                        // replace all whitespaces to space
                        var cmd = area.getText().replaceAll("\\s+", " ").trim();
                        parser.parseInput(cmd);
                        logs.add(cmd);

                        controlPackage.getUIPackage().getMyTextInput().getErrorConsole().setText(null);
                    } catch (Exception e) {
                        controlPackage.getUIPackage().getMyTextInput().getErrorConsole().setText(e.getMessage());
                    }
                    area.clear();
                }
            }
        });
    }

    private void myActiveIDChooserHandler(ActionEvent e){
        var chooser = controlPackage.getUIPackage().getMyPrefer().getActiveTurtleChooser();
        TurtleView turtleView = controlPackage.getTurtleView(chooser.getValue());
        controlPackage.getUIPackage().getMyPrefer().updatePreferenceValues(turtleView.getSpeed(),
                turtleView.getModelRot(), turtleView.getNumericPenStatus());
    }


    @Override
    public void hasAddedTurtles(List<Integer> turtleIDs) {
        for (int id: turtleIDs){
            controlPackage.getUIPackage().getMyPaper().addTurtleView(id);
            setupNewTurtle(id);
        }
    }
}