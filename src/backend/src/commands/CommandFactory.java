package commands;

import parsing.Interpreter;
import parsing.Token;
import utility.InvalidArgumentException;
import utility.InvalidNumberOfArgumentsException;

import java.util.*;

/**
 * @author Peter Ciporin (pbc9)
 * @author Seung-Woo Choi (sc442)
 *
 * This class is responsible for generating the proper instances of Command
 */
public class CommandFactory {

    private static final String[] TURTLE_COMMANDS = {"Forward", "Backward", "Left", "Right", "SetHeading", "SetTowards", "SetPosition", "PenDown", "PenUp", "ShowTurtle", "HideTurtle", "Home", "ClearScreen"};
    private static final String[] TURTLE_QUERIES = {"XCoordinate", "YCoordinate", "Heading", "IsPenDown", "IsShowing"};
    private static final String[] MATH_OPERATIONS = {"Sum", "Difference", "Product", "Quotient", "Remainder", "Minus", "Random", "Sine", "Cosine", "Tangent", "ArcTangent", "NaturalLog", "Power", "Pi"};
    private static final String[] BOOLEAN_OPERATIONS = {"LessThan", "GreaterThan", "Equal", "NotEqual", "And", "Or", "Not"};
    private static final String[] CUSTOM_ASSIGNMENTS = {"MakeVariable", "Repeat", "DoTimes", "For", "If", "IfElse", "MakeUserInstruction"};

    private HashSet<String> turtleCommands;
    private HashSet<String> turtleQueries;
    private HashSet<String> mathOperations;
    private HashSet<String> booleanOperations;
    private HashSet<String> customAssignments;
    private ResourceBundle myErrors;

    private Interpreter interpreter;

    public CommandFactory(Interpreter interp) {

        turtleCommands = new HashSet<>(Arrays.asList(TURTLE_COMMANDS));
        turtleQueries = new HashSet<>(Arrays.asList(TURTLE_QUERIES));
        mathOperations = new HashSet<>(Arrays.asList(MATH_OPERATIONS));
        booleanOperations = new HashSet<>(Arrays.asList(BOOLEAN_OPERATIONS));
        customAssignments = new HashSet<>(Arrays.asList(CUSTOM_ASSIGNMENTS));
        interpreter = interp;
        myErrors = ResourceBundle.getBundle("resources/Errors");

    }

    public Command getNewCommand(Token t) {
        if (t.getTokenType().equals("Command") && t.getNumberOfChildren() == t.getMaxArgCount()) { // TODO switch back

            String a1,a2,a3,a4;
            if(t.getChildren().size() > 0) a1 = t.getChildren().get(0).getTokenValue();
            else a1 = null;
            if(t.getChildren().size() > 1) a2 = t.getChildren().get(1).getTokenValue();
            else a2 = null;
            if(t.getChildren().size() > 2) a3 = t.getChildren().get(2).getTokenValue();
            else a3 = null;

            var variablesMap = interpreter.getCustomVariables();
            var commandsMap = interpreter.getCustomCommands();


            if(variablesMap.containsKey(a1) /*&& !t.getChildren().get(0).getTokenType().equals("Variable")*/) a1 = Double.toString(variablesMap.get(a1));
            if(variablesMap.containsKey(a2)) a2 = Double.toString(variablesMap.get(a2));

            if(t.getTokenValue().equals("DoTimes")){
                List<Token> varList = t.getChildren().get(0).getChildren();
                List<Token> commandList = t.getChildren().get(1).getChildren();
                commandList.remove(commandList.get(commandList.size()-1));


                String var = varList.get(0).getTokenValue();

                variablesMap.put(var, 1.0);

                double limit;
                if(varList.get(1).getTokenType().equals("Command")) {
                     limit = getNewCommand(varList.get(1)).getResult();
                }else{
                    limit = Double.parseDouble(varList.get(1).getTokenValue());
                }

                DoTimesCommand loop = new DoTimesCommand(var, limit);

                while(variablesMap.get(var) <= limit) {
                    loop.addCommandList(getCommandList(commandList));
                    variablesMap.put(var, variablesMap.get(var) + 1);
                }

                return loop;
            }
            else if(t.getTokenValue().equals("For")){
                List<Token> varList = t.getChildren().get(0).getChildren();
                List<Token> commandList = t.getChildren().get(1).getChildren();
                commandList.remove(commandList.get(commandList.size()-1));

                String var = varList.get(0).getTokenValue();

                double start;
                if(varList.get(1).getTokenType().equals("Command")) {
                    start = getNewCommand(varList.get(1)).getResult();
                }else{
                    start = Double.parseDouble(varList.get(1).getTokenValue());
                }

                double end;
                if(varList.get(2).getTokenType().equals("Command")) {
                    end = getNewCommand(varList.get(2)).getResult();
                }else{
                    end = Double.parseDouble(varList.get(2).getTokenValue());
                }

                double increment;
                if(varList.get(3).getTokenType().equals("Command")) {
                    increment = getNewCommand(varList.get(3)).getResult();
                }else{
                    increment = Double.parseDouble(varList.get(3).getTokenValue());
                }

                variablesMap.put(var, start);

                ForCommand loop = new ForCommand(var, start,end,increment);

                while(variablesMap.get(var) <= end) {
                    loop.addCommandList(getCommandList(commandList));
                    variablesMap.put(var, variablesMap.get(var) + increment);
                }

//                while(variablesMap.get(var) <= end) {
//                    for (Token c : commandList) {
//                        if (c.getTokenType().equals("ListEnd")) break;
//                        loop.addCommand(getNewCommand(c));
//                    }
//                    variablesMap.put(var, variablesMap.get(var) + increment);
//                }

                return loop;
            }
            else if(t.getTokenValue().equals("Repeat")) {
                double rep = Double.parseDouble(a1);
                List<Token> list = t.getChildren().get(1).getChildren();
                RepeatCommand loop = new RepeatCommand(rep);

                return loop;
            }
            else if (mathOperations.contains(t.getTokenValue()) || booleanOperations.contains(t.getTokenValue())) {
                Operation op;
                switch(t.getValidArgCount()){
                    case 0:
                        op = new Operation(t.getTokenValue());
                        break;
                    case 1:
                        op = new Operation(t.getTokenValue(), Double.parseDouble(a1));
                        break;
                    case 2:
                        op = new Operation(t.getTokenValue(), Double.parseDouble(a1), Double.parseDouble(a2));
                        break;
                    default:
                        op = new Operation(t.getTokenValue());
                        break;
                }
                return op;
            }
            else if (t.getTokenValue().equals("MakeVariable")) {
                //TODO: throw an actual error here
                if (!t.getChildren().get(0).getTokenType().equals("Variable") || t.getChildren().get(0).getTokenValue().isEmpty()) {
                    throw new InvalidArgumentException("Not a variable: " + t.getChildren().get(0));
                }
                return new CustomVariable(t.getTokenValue(),a1, Double.parseDouble(a2));
            }
            else if (t.getTokenValue().equals("MakeUserInstruction")) {
                List<String> arguments = new ArrayList<>();
                List<Token> toExecute = t.getChildren().get(2).getChildren();

                for(Token a : t.getChildren().get(1).getChildren()){
                    if(a.getTokenType().equals("ListEnd")) break;
                    arguments.add(a.getTokenValue());
                }

                CustomCommand c = new CustomCommand(t.getTokenValue(), t.getChildren().get(0).getLexeme(), arguments, toExecute);
                c.removeListEndToken();
                return c;
            }
            else if (t.getTokenValue().equals("Tell")) {
                return new Command(t.getTokenValue(), Double.parseDouble(t.getChildren().get(0).getChildren().get(0).getTokenValue()));
            }
            else {
                Command cmd;
                switch(t.getValidArgCount()){
                    case 0:
                        cmd = new Command(t.getTokenValue());
                        break;
                    case 1:
                        cmd = new Command(t.getTokenValue(), Double.parseDouble(a1));
                        break;
                    case 2:
                        cmd = new Command(t.getTokenValue(), Double.parseDouble(a1), Double.parseDouble(a2));
                    break;
                    case 3:
                        cmd = new Command(t.getTokenValue(), Double.parseDouble(a1), Double.parseDouble(a2), Double.parseDouble(a3));
                    default:
                        cmd = new Command(t.getTokenValue());
                    break;
                }
                return cmd;
            }
        }

         throw new InvalidNumberOfArgumentsException(myErrors.getString("InvalidNumberOfArguments") + " " + t);

    }


    /**
     *
     * I GOT LAZY
     */

    public List<Command> getCommandList(List<Token> tokens){        // Goes through the trees and creates a commandlist
        ArrayList<Command> commands = new ArrayList<>();

        for(Token t: tokens){
            //left branch
            pullCommandsFromBranch(t, commands);
            //right branch
            pullCommandsFromBranch(t, commands);
            pullHeadCommand(t, commands);
        }

        return commands;

    }

    private void pullCommandsFromBranch(Token treeRoot, ArrayList<Command> commands) {
        Token currentCommand = getBottomCommandToken(treeRoot);

        while(currentCommand.getParent() != null && currentCommand.getParent().getTokenType().equals("Command")) {
            Command newCommand = getNewCommand(currentCommand);
            commands.add(newCommand);
            currentCommand.constantize(newCommand.getResult());

            currentCommand = currentCommand.getParent();
        }
    }

    private void pullHeadCommand(Token treeRoot, ArrayList<Command> commands) {
        Token currentCommand = getBottomCommandToken(treeRoot);
        Command newCommand = getNewCommand(currentCommand);
        commands.add(newCommand);       // add head token
    }

    private ArrayList<Token> getTokenTrees(List<Token> tokens) {
        ArrayList<Token> tokenTrees = new ArrayList<>();

        Token cmd = tokens.get(0);

        Token currToken;

        int index = 1;

        while (index < tokens.size()) {
            currToken = tokens.get(index);

            if (cmd.getNumberOfChildren() < cmd.getMaxArgCount() && !cmd.isFull()) {
                cmd.addChild(currToken);
            }else if(cmd.getParent() != null) {
                cmd = cmd.getParent();
                continue;
            }

            if (currToken.getTokenType().equals("Command")
                    && !currToken.getTokenValue().equals("Custom")
                    || currToken.getTokenType().equals("ListStart")) {
                cmd = currToken;
            }

            // bounce back up to the top command that has no parent, add to list
            if(cmd.isFull()){
                Token temp = cmd;
                while(temp.getParent() != null&&temp.getParent().isFull()){
                    temp = temp.getParent();
                }

                if(temp.isFull() && temp.getParent() == null) tokenTrees.add(temp);

//                Token highestParent = cmd.getHighestParent();
//                if(highestParent.isFull()){
//                    tokenTrees.add(highestParent);
//                }
            }

            index++;
        }
        if(tokens.size() == 1) tokenTrees.add(cmd);


        return tokenTrees;
    }

//    private Token getTokenTree(List<Token> tokens) {
//        Token prev = tokens.get(0);
//        Token curr;
//        int index = 1;
//
//        while (index < tokens.size()) {
//            curr = tokens.get(index);
//            if (prev.getNumberOfChildren() == 0) {
//                prev.setLeftChild(curr);
//            }
//            else if (prev.getNumberOfChildren() == 1) {
//                prev.setRightChild(curr);
//            }
//            else {
//                prev = prev.getParent();
//                continue;
//            }
//
//            if (curr.getTokenType().equals(COMMAND_TOKEN_TYPE)) {
//                prev = curr;
//            }
//
//            index++;
//        }
//
//        return tokens.get(0);
//    }

    private Token getBottomCommandToken(Token head){
        Token curr = head;

        while (curr.getNumberOfChildren() > 0) {
            List<Token> childrenList = curr.getChildren();

            boolean foundChildCommand = false;
            for(Token child: childrenList){
                if(child.getTokenType().equals("Command")){
                    curr = child;
                    foundChildCommand = true;
                    break;
                }
            }
            if (!foundChildCommand) {break;}

        }
        return curr;
    }



}
