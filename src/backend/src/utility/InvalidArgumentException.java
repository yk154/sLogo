package utility;

public class InvalidArgumentException extends RuntimeException {
    /**
     * Create an exception based on trying to run commands with invalid arguments (given the command).
     * @author Natalie
     */
    public InvalidArgumentException(String message, Object ... values) {
        super(String.format(message, values));
    }
}
