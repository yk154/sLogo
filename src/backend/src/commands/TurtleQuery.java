package commands;

/**
 * @author Peter Ciporin (pbc9)
 *
 * This subclass represents a Command that queries data from the ControlTurtleMovement
 */
public class TurtleQuery extends Command {

    public TurtleQuery(String action, double value) {
        super(action, value);
    }
}
