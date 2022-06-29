package farpost.task.exceptions;

public class UnexpectedArgumentException extends RuntimeException {
    public UnexpectedArgumentException(String arg) {
        super("Unexpected parameter: " + arg);
    }
}
