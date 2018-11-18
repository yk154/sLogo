package commands;

import parsing.Token;

import java.util.ArrayList;
import java.util.List;

public class CustomCommand extends Command {

    private String commandName;
    private List<Token> toExecute;
    private List<Token> originalExecute;
    private List<String> arguments;


    public CustomCommand(String action, String commandName, List<String> args, List<Token> toExecute) {
        super(action);
        setCommandName(commandName);
        this.arguments = args;
        this.toExecute = toExecute;
        this.originalExecute = toExecute;
    }

    public void setCommandName(String newName) {
        this.commandName = newName;
    }

    public void setToExecute(List<Token> toExecute) {
        this.toExecute = toExecute;
    }

    public String getCommandName() {return this.commandName;}

    public List<Token> getToExecute() {return this.toExecute;}

    public void resetToExecute() { toExecute = originalExecute;}

    public List<String> getArguments() {return this.arguments;}

    public int getNumArguments() {return this.arguments.size();}

    public void removeListEndToken() {
        if (this.toExecute!=null) {
            this.toExecute.remove(this.toExecute.get(this.toExecute.size() - 1));
        }
    }

    @Override
    public String toString() {
        return "<Command Name: " + commandName + ", Arguments: " + printArguments() + ">";
        }


    private String printArguments() {
        if (arguments.isEmpty()) {
            return "No Arguments";
        }
        StringBuilder toReturn = new StringBuilder();
        for (String arg : arguments) {
            toReturn.append(arg + " ");
        }
        return toReturn.toString();
    }

}