package utility;

public class InvalidNumberOfArgumentsException extends RuntimeException {

    /**
     * Create an exception based on trying to move the turtle outside of the paper's boundaries.
     * @author Natalie
     */
    public InvalidNumberOfArgumentsException(String message, Object ... values) {
        super(String.format(message, values));
    }
}
