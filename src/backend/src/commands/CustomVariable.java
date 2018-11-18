package commands;

/**
 * @author Peter Ciporin (pbc9)
 *
 * This subclass represents a Command that assigns or updates the value of a user-defined variable or command
 */
public class CustomVariable extends Command {

    private String variableName;
    private double variableValue;

    public CustomVariable(String action, String variableName, double variableValue) {
        super(action);
        setVariableName(variableName);
        setVariableValue(variableValue);
    }

    public void setVariableName(String newName) {
        this.variableName = newName;
    }

    public void setVariableValue(double newValue) {
        this.variableValue = newValue;
    }

    public String getVariableName() {return this.variableName;}

    public double getVariableValue() {return this.variableValue;}

    @Override
    public String toString() {
        return "<Variable Name: " + variableName + ", Variable Value: " + variableValue + ">";
    }

}
