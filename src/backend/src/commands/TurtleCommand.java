package commands;

/**
 * @author Peter Ciporin (pbc9)
 *
 * This subclass represents a Command that alters the state of the ControlTurtleMovement
 */
public class TurtleCommand extends Command {

    public TurtleCommand(String action, double value){
        super(action, value);
    }

}
