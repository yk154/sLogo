package parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Ciporin (pbc9)
 * @author Seung-Woo Choi (sc442)
 *
 * This class represents each "lexeme" within a user's input.  A lexeme is a single identifiable sequence of characters, for example, keywords (such as class, func, var, and while), literals (such as numbers and strings), identifiers, operators, or punctuation characters (such as {, (, and .).
 * A token MUST be one of the types enumerated in languages/Syntax.properties
 */
public class Token {
    private String tokenType;
    private String value;
    private List<Token> childrenList;
    private Token parent;
    private String lexeme;

    private final String MAKE_USER_INSTRUCTION_VALUE = "MakeUserInstruction";

    private int maxArgCount;

    public Token(String type, String val, int maxArg, String lexeme) {
        this.tokenType = type;
        this.value = val;
        this.maxArgCount = maxArg;
        this.lexeme = lexeme;

        childrenList = new ArrayList<>();
    }

    public List<Token> getChildren(){
        return this.childrenList;
    }

    public void setParent(Token parent) {
        this.parent = parent;
    }


    public Token getParent() {
        return this.parent;
    }

    public Token getHighestParent(){
        Token currentParent = this;

        while(currentParent.getParent() != null){
            currentParent = currentParent.getParent();
        }

        return currentParent;
    }

    public int getNumberOfChildren() {

        return childrenList.size();
    }

    public int getValidArgCount(){
        int numArgCount = 0;

        for(Token t: childrenList){
            if(t.isValidArgument()) numArgCount++;
        }

        return numArgCount;
    }

    public boolean isFull(){
        if(getTokenType().equals("ListStart")) return isListEnded();  // It's full if it contains a ] token

        return getNumberOfChildren() == getMaxArgCount();
    }

    private boolean isListEnded(){
        for(Token t : childrenList){
            if(t.getTokenType().equals("ListEnd")) return true;
        }
        return false;
    }

    public void addChild(Token child){
        child.setParent(this);
        this.childrenList.add(child);
    }

    private boolean isValidArgument() {
        return this.getTokenType().equals("Constant") || this.getTokenType().equals("Variable")
                || this.getTokenType().equals("ListEnd")
                || this.getTokenType().equals("ListStart")
                || this.getTokenValue().equals("Custom");
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public String getTokenValue() {
        return this.value;
    }

    public int getMaxArgCount(){
        return maxArgCount;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "<Token Type: " + tokenType + ", Token Value: " + value + ">";
    }

    public void constantize(double result){
        tokenType = "Constant";
        value = Double.toString(result);
    }


}