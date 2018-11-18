package control;

import control.command.Controlbuttons;
import control.turtle.ControlPreferences;
import view.turtle.TurtleView;
import view.UIsetup;

/**
 * ControlSetup
 *
 * Control all the features
 *
 * @author Amy Kim
 */

public class ControlSetup {
    private UIsetup uiPackage;

    private ControlPreferences controlPreferences;
    private Controlbuttons controlButtons;

    /**
     *
     * @param uiPackage is package of the features
     */
    public ControlSetup(UIsetup uiPackage) {
        this.uiPackage = uiPackage;

        controlPreferences = new ControlPreferences(uiPackage.getMyPrefer(), uiPackage.getMyPaper());
        controlButtons = new Controlbuttons(uiPackage.getMyButtons());
    }

    public ControlPreferences getControlPreferences() { return controlPreferences; }

    public TurtleView getTurtleView(int id){
        return uiPackage.getMyPaper().getMyTurtleView(id);
    }

    public UIsetup getUIPackage(){
        return uiPackage;
    }
}