package commands;

import java.util.ArrayList;
import java.util.List;

public class RepeatCommand extends Command {

    private double numIterations;
    private List<Command> toExecute;

    public RepeatCommand(double iterations){
        super("Repeat");
        this.numIterations = iterations;
        this.toExecute = new ArrayList<>();
    }

    public double getNumIterations() {
        return this.numIterations;
    }

    public List<Command> getToExecute() {
        return this.toExecute;
    }

    public void setToExecute(List<Command> cmds) {
        this.toExecute = cmds;
    }

    public void addCommand(Command c) {
        toExecute.add(c);
    }

}