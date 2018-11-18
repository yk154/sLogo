package parsing;

import commands.*;
import utility.InvalidCommandException;
import utility.InvalidNumberOfArgumentsException;

import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Peter Ciporin (pbc9)
 * @author Seung-Woo Choi (sc442)
 * @author Robert C. Duvall
 *
 *
 * This class is responsible for parsing a String of user input and generating the proper Commands.
 * It also maintains maps of user-defined variables and commands.
 * Some methods taken from Professor Duvall's spike code.
 */
public class CmdParser {

    private static final String ARGUMENTS_FILE_PATH = "src/backend/src/languages/Arguments.properties";
    private static final String PROPERTIES_DIRECTORY_PATH = "languages/";
    private static final String SYNTAX_PROPERTIES_FILE_NAME = "Syntax";
    private static final String DEFAULT_LANGUAGE_FILE_NAME = "English";
    private static final String COMMAND_TOKEN_TYPE = "Command";
    private static final String CUSTOM_TOKEN_TYPE = "Custom";

    private List<Command> commandList;
    private Properties argProps;
    private ResourceBundle myErrors;

    // "types" and the regular expression patterns that recognize those types
    // note, it is a list because order matters (some patterns may be more generic)
    private List<Map.Entry<String, Pattern>> myTypes;
    private List<Map.Entry<String, Pattern>> myCommands;

    private String[] currentInput;
    private int currentPosition;
    private CommandFactory myFactory;


    private Interpreter interpreter;


    /**
     *  This global variable tells the parser whether it is making a list or not
     */

    boolean isMakingList;

    public CmdParser(Interpreter interp) {

        isMakingList = false;

        this.myErrors = ResourceBundle.getBundle("resources/Errors");

        this.commandList = new LinkedList<>();
        this.myTypes = new ArrayList<>();
        this.myCommands = new ArrayList<>();

        this.argProps = new Properties();
        try {
            argProps.load(new FileInputStream(ARGUMENTS_FILE_PATH));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Move this somewhere else?
        addPatterns(PROPERTIES_DIRECTORY_PATH + SYNTAX_PROPERTIES_FILE_NAME, myTypes);


        this.resetCurrentInput("");
        this.setLanguage(DEFAULT_LANGUAGE_FILE_NAME);
        this.interpreter = interp;
        this.myFactory = new CommandFactory(interpreter);
    }

    public CmdParser() {

        isMakingList = false;

        this.myErrors = ResourceBundle.getBundle("resources/Errors");

        this.commandList = new LinkedList<>();
        this.myTypes = new ArrayList<>();
        this.myCommands = new ArrayList<>();

        this.argProps = new Properties();
        try {
            argProps.load(new FileInputStream(ARGUMENTS_FILE_PATH));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Move this somewhere else?
        addPatterns(PROPERTIES_DIRECTORY_PATH + SYNTAX_PROPERTIES_FILE_NAME, myTypes);


        this.resetCurrentInput("");
        this.setLanguage(DEFAULT_LANGUAGE_FILE_NAME);
        this.interpreter = new Interpreter();
        this.myFactory = new CommandFactory(interpreter);
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setLanguage(String language) {
        myCommands.clear();
        addPatterns(PROPERTIES_DIRECTORY_PATH + language, myCommands);
    }

    /**
     * Adds the given resource file to this language's recognized types
     */
    public void addPatterns (String syntax, List<Map.Entry<String, Pattern>> toPopulate) {
        var resources = ResourceBundle.getBundle(syntax);
        for (var key : Collections.list(resources.getKeys())) {
            //System.out.println("key is " + key);
            var regex = resources.getString(key);
            //System.out.println("regex is " + regex);
            toPopulate.add(new AbstractMap.SimpleEntry<>(key,
                    // THIS IS THE IMPORTANT LINE
                    Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
        }
    }

    /**
     **
     * Tokenizes a user's text input and calls on interpreter to execute commands
     *
     * @param input A string of the user's input
     */

    public void parseInput(String input){
        System.out.println(input);
        try {
            resetCurrentInput(input);
            tokenize();
            this.interpreter.executeCommands(this.commandList, false);
        } catch (Exception e){
            throw e;
        }
    }

    public void resetCurrentInput(String input) {
        this.currentInput = input.split(" "); // assuming one command
        this.currentPosition = 0;
        this.commandList.clear();
    }

    /**
     * Converts a user input string into Token objects
     */
    public void tokenize() {
        List<Token> tokens = new LinkedList<>();
        while (this.currentPosition < this.currentInput.length) {
            try {
                Token next = nextToken();
                System.out.println(next);
                tokens.add(next);
                this.currentPosition++;
            } catch (Exception e){
                throw e;            // TODO: Implement error?
            }
        }

        List<Command> cmds = getCommandList(tokens, true);
        System.out.println("Commands are: ");
        this.commandList = cmds;
        for (Command c : commandList) {System.out.println(c);}
    }

    /**
     *
     * @return the token representation of a given lexeme
     */
    public Token nextToken() {
        String lexeme = currentInput[currentPosition];
        try {
            String tokenType = getSymbol(lexeme, myTypes);
            String tokenValue = (tokenType.equals(COMMAND_TOKEN_TYPE)) ? getSymbol(lexeme, myCommands) : lexeme;

            String numArgs = this.argProps.getProperty(tokenValue);
            int maxArg = (numArgs != null) ? Integer.parseInt(numArgs) : 0;
            if(tokenValue.equals( "Custom")) maxArg = 0;              // A custom command should have no arguments, for now
            if(tokenType.equals( "ListStart")) maxArg = 1000;             // ListStart should have unlimited arguments
            if(tokenType.equals( "ListEnd")) maxArg = 0;

            if (interpreter.getCustomCommands().containsKey(lexeme)) {
                maxArg = interpreter.getCustomCommands().get(lexeme).getNumArguments();
            }


            return new Token(tokenType, tokenValue, maxArg, lexeme);
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * Returns language's type associated with the given text if one exists
     */
    public String getSymbol (String text, List<Map.Entry<String, Pattern>> toCheck) {
        for (var e : toCheck) {
            if (match(text, e.getValue())) {
                return e.getKey();
            }
        }
        return CUSTOM_TOKEN_TYPE;
    }

    /**
     * Returns true if the given text matches the given regular expression pattern
     */
    private boolean match (String text, Pattern regex) {
        // THIS IS THE IMPORTANT LINE
        return regex.matcher(text).matches();
    }

    /**
     *
     * @param tokens the tokens from which to generate a list of commands
     * @return the list of commands to be executed
     */
    public List<Command> getCommandList(List<Token> tokens, boolean shouldGenerateNewTrees){        // Goes through the trees and creates a commandlist
        ArrayList<Command> commands = new ArrayList<>();

        List<Token> trees;
        if (shouldGenerateNewTrees) {
            trees = getTokenTrees(tokens);
        }
        else {
            trees = tokens;
        }

        ArrayList<Command> loopcommands = new ArrayList<>();

        for (Token t : trees) {


            if (t.getTokenValue().equals("MakeUserInstruction")) {
                Token head = tokens.get(0);
                CustomCommand custom = (CustomCommand) myFactory.getNewCommand(head);
                commands.add(custom);
            }
            else if (t.getTokenValue().equals("Tell")) {
                Command c = myFactory.getNewCommand(t);
                commands.add(c);
            }
            else if(t.getTokenValue().equals("For")){
                Token head = t;

                ForCommand loop = (ForCommand) myFactory.getNewCommand(head);

                commands.add(loop);
            }
            else if (t.getTokenValue().equals("DoTimes")) {
                Token head = t;

                DoTimesCommand loop = (DoTimesCommand) myFactory.getNewCommand(head);

                commands.add(loop);
            }

            else if(t.getTokenValue().equals("Repeat")){
                Token head = t;
                List<Token> list = head.getChildren().get(1).getChildren();
                list.remove(list.get(list.size()-1));       // remove last element

                RepeatCommand loop = (RepeatCommand) myFactory.getNewCommand(head);
                loopcommands.addAll(getCommandList(list, false));
                loop.setToExecute(loopcommands);
                commands.add(loop);
            }

            else if (t.getTokenValue().equals("If")) {
                Token head = tokens.get(0);
                Command expression = myFactory.getNewCommand(head.getChildren().get(0));
                if (expression.getResult() == 1) {
                    List<Token> toExecute = head.getChildren().get(1).getChildren();
                    toExecute.remove(toExecute.get(toExecute.size() - 1)); //remove ']' token
                    commands.addAll(getCommandList(toExecute, false));
                }
            }

            else if (t.getTokenValue().equals("IfElse")) {
                Token head = tokens.get(0);
                Command expression = myFactory.getNewCommand(head.getChildren().get(0));
                if (expression.getResult() == 1) {
                    List<Token> toExecute = head.getChildren().get(1).getChildren();
                    toExecute.remove(toExecute.get(toExecute.size() - 1)); //remove ']' token
                    commands.addAll(getCommandList(toExecute, false));
                }
                else {
                    List<Token> toExecute = head.getChildren().get(2).getChildren();
                    toExecute.remove(toExecute.get(toExecute.size() - 1)); //remove ']' token
                    commands.addAll(getCommandList(toExecute, false));
                }
            }

            else if (t.getTokenValue().equals("Custom")) {
                if (interpreter.getCustomCommands().containsKey(t.getLexeme())) {
                    CustomCommand cust = interpreter.getCustomCommands().get(t.getLexeme());

                    for (int i=0; i<t.getChildren().size(); i++) {
                        double argVal = Double.parseDouble(t.getChildren().get(i).getTokenValue());
                        String argName = cust.getArguments().get(i);
                        interpreter.addCustomVariable(argName, argVal);
                    }

                    commands.addAll(getCommandList(cust.getToExecute(), false));

                    for (String argName : cust.getArguments()) {
                        interpreter.getCustomVariables().remove(argName);
                    }

                    cust.resetToExecute();
                }
                else {
                    throw new InvalidCommandException(myErrors.getString("InvalidCommand"));
                }
            }

            else {

                //left branch
                pullCommandsFromBranch(t, commands);
                //right branch
                pullCommandsFromBranch(t, commands);
                pullHeadCommand(t, commands);
            }
        }



        return commands;

    }

    private void pullCommandsFromBranch(Token treeRoot, ArrayList<Command> commands) {
        Token currentCommand = getBottomCommandToken(treeRoot);

        while(currentCommand.getParent() != null && currentCommand.getParent().getTokenType().equals("Command")) {
            Command newCommand = myFactory.getNewCommand(currentCommand);
            commands.add(newCommand);
            currentCommand.constantize(newCommand.getResult());

            currentCommand = currentCommand.getParent();
        }
    }

    private void pullHeadCommand(Token treeRoot, ArrayList<Command> commands) {
        Token currentCommand = getBottomCommandToken(treeRoot);
        Command newCommand = myFactory.getNewCommand(currentCommand);
        commands.add(newCommand);       // add head token
    }

    private ArrayList<Token> getTokenTrees(List<Token> tokens) {
        ArrayList<Token> tokenTrees = new ArrayList<>();

        Token cmd = tokens.get(0);

        Token currToken;

        int index = 1;
        int expectedArgs = 0;

        while (index < tokens.size()) {
            currToken = tokens.get(index);

            if (cmd.getNumberOfChildren() < cmd.getMaxArgCount() && !cmd.isFull()) {
                cmd.addChild(currToken);
            }else if(cmd.getParent() != null) {
                cmd = cmd.getParent();
                continue;
            }

            if (currToken.getTokenType().equals(COMMAND_TOKEN_TYPE)
                    && !currToken.getTokenValue().equals("Custom")
                    || currToken.getTokenType().equals("ListStart")) {
                cmd = currToken;
            }
            if (currToken.getTokenType() == COMMAND_TOKEN_TYPE) {
                expectedArgs += currToken.getMaxArgCount() + 1; // expected number of arguments plus one more for the command
            }

            // bounce back up to the top command that has no parent, add to list
            if(cmd.isFull()){
                Token temp = cmd;
                while(temp.getParent() != null&&temp.getParent().isFull()){
                    temp = temp.getParent();
                }

                if(temp.isFull() && temp.getParent() == null) tokenTrees.add(temp);

            }

            index++;
        }
        if(tokens.size() == 1) tokenTrees.add(cmd);

        if (tokens.size()!=expectedArgs) {
            //System.out.println("expected " + expectedArgs + " actual " + tokens.size());
            //throw new InvalidNumberOfArgumentsException(myErrors.getString("InvalidNumberOfArguments"));}
        }


        return tokenTrees;
    }



    private Token getBottomCommandToken(Token head){
        Token curr = head;

        while (curr.getNumberOfChildren() > 0) {
            List<Token> childrenList = curr.getChildren();

            boolean foundChildCommand = false;
            for(Token child: childrenList){
                if(child.getTokenType().equals(COMMAND_TOKEN_TYPE)){
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
