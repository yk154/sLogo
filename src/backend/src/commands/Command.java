package commands;

import parsing.Token;

/**
 * @author Peter Ciporin (pbc9)
 * @author Natalie Le
 * @author Seung-Woo Choi (sc442)
 *
 * This class is the parent for all Command objects in our program
 */
public class Command {

    private String action;
    private double param1;
    private double param2;
    private double param3;
    private double param4;

    private double result;

    public Command(String action, double param1, double param2, double param3){
        this.action = action;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;

        this.result = param1;
    }

    public Command(String action, double param1, double param2){
        this.action = action;
        this.param1 = param1;
        this.param2 = param2;

        this.result = param1;
    }

    public Command(String action, double param) {
        this.action = action;
        this.param1 = param;

        this.result = param;
    }

    public Command(String action) {
        this.action = action;
    }

    public String getAction(){
        return action;
    }

    public double getParam1(){
        return param1;
    }

    public double getParam2() { return param2; }

    public double getParam3() { return param3; }

    public void setResult(double res) {this.result = res;}

    public double getResult() {return result;}

    @Override
    public String toString() {
        return "<Action: " + action + ", Result: " + result + ">";
    }

}