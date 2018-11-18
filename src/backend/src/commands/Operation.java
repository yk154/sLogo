package commands;

import parsing.Token;

import java.util.Random;

/**
 * @author Peter Ciporin (pbc9)
 * @author Seung-Woo Choi (sc442)
 *
 * This subclass represents a Command to perform a Math or Boolean operation
 */
public class Operation extends Command {

    public Operation(String op, double param1, double param2) {
        super(op, param1, param2);
        execute();
    }

    public Operation(String op, double param){
        super(op, param);
        execute();
    }

    public Operation(String op){
        super(op);
        execute();
    }

    public void execute() {
        switch (getAction()) {
            case "Sum":
                setResult(getParam1() + getParam2());
                break;
            case "Difference":
                setResult(getParam1() - getParam2());
                break;
            case "Product":
                setResult(getParam1() * getParam2());
                break;
            case "Quotient":
                setResult(getParam1() / getParam2());
                break;
            case "Remainder":
                setResult(getParam1() % getParam2());
                break;
            case "Minus":
                setResult(getParam1() * -1);
                break;
            case "Random":
                Random rand = new Random();
                setResult(rand.nextInt((int)getParam1()));
                break;
            case "Sine":
                double degrees = getParam1();
                setResult(Math.sin(Math.toRadians(degrees)));
                break;
            case "Cosine":
                degrees = getParam1();
                setResult(Math.cos(Math.toRadians(degrees)));
                break;
            case "Tangent":
                degrees = getParam1();
                setResult(Math.tan(Math.toRadians(degrees)));
                break;
            case "ArcTangent":
                degrees = getParam1();
                setResult(Math.atan(Math.toRadians(degrees)));
                break;
            case "NaturalLog":
                setResult(Math.log(getParam1()));
                break;
            case "Power":
                setResult(Math.pow(getParam1(), getParam2()));
                break;
            case "Pi":
                setResult(Math.PI);
                break;
            case "LessThan":
                double res = (getParam1() < getParam2()) ? 1 : 0;
                setResult(res);
                break;
            case "GreaterThan":
                res = (getParam1() > getParam2()) ? 1 : 0;
                setResult(res);
                break;
            case "Equal":
                res = (getParam1() == getParam2()) ? 1 : 0;
                setResult(res);
                break;
            case "NotEqual":
                res = (getParam1() != getParam2()) ? 1 : 0;
                setResult(res);
                break;
            case "And":
                res = (getParam1()!=0 && getParam2()!=0) ? 1 : 0;
                setResult(res);
                break;
            case "Or":
                res = (getParam1()!=0 || getParam2()!=0) ? 1 : 0;
                setResult(res);
                break;
            case "Not":
                res = (getParam1()==0) ? 1 : 0;
                setResult(res);
                break;
            default:
                break;
        }
    }


}
