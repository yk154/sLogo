package commands;

import java.util.ArrayList;
import java.util.List;

public class DoTimesCommand extends Command {

    private String variable;
    private double limit;
    private List<Command> toExecute;

    public DoTimesCommand(String var, double limit){
        super("DoTimes");

        this.variable = var;
        this.limit = limit;
        this.toExecute = new ArrayList<>();
    }

    public String getVariable(){ return this.variable;}

    public double getLimit() {
        return this.limit;
    }

    public List<Command> getToExecute() {
        return this.toExecute;
    }

    public void addCommand(Command c) {
        toExecute.add(c);
    }

    public void addCommandList(List<Command> c){
        toExecute.addAll(c);
    }

}