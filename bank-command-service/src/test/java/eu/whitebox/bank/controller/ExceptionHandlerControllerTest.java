package eu.whitebox.bank.controller;

import eu.whitebox.bank.exception.OverdraftLimitExceededException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

/**
 * @author Ranjith
 */
public class ExceptionHandlerControllerTest {

    private ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();

    @Test
    public void testIllegalArgumentException_shouldReturnBadRequest() {
        final String message = "Invalid input";
        final IllegalArgumentException exception = new IllegalArgumentException(message);

        final ResponseEntity<String> response = exceptionHandlerController.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    public void testOverdraftLimitExceededException_shouldReturnUnprocessableEntity() {
        final String message = "Overdraft limit exceeded";
        final OverdraftLimitExceededException exception = new OverdraftLimitExceededException(message);

        final ResponseEntity<String> response = exceptionHandlerController.handleOverdraftLimitExceededException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}
