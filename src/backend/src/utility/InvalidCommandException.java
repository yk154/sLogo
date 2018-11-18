package utility;

public class InvalidCommandException extends RuntimeException {

    /**
     * Create an exception based on trying to run commands that don't exist.
     * @author Natalie
     */
    public InvalidCommandException(String message, Object ... values) {
        super(String.format(message, values));
    }
}
