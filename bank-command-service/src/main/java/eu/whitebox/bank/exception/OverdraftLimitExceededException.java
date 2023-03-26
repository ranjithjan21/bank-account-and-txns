package eu.whitebox.bank.exception;

/**
 * @author Ranjith
 */
public class OverdraftLimitExceededException extends RuntimeException {
    public OverdraftLimitExceededException(String message) {
        super(message);
    }
}
