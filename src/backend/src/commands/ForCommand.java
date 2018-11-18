package commands;

import java.util.ArrayList;
import java.util.List;

public class ForCommand extends Command {

    private String variable;
    private double start;
    private double end;
    private double increment;
    private List<Command> toExecute;

    public ForCommand(String var, double start, double end, double increment){
        super("For");
        this.variable = var;
        this.start = start;
        this.end = end;
        this.increment = increment;
        this.toExecute = new ArrayList<>();
    }

    public String getVariable(){ return this.variable;}

    public double getEnd() {
        return this.end;
    }

    public double getStart(){
        return this.start;
    }

    public double getIncrement(){
        return this.increment;
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