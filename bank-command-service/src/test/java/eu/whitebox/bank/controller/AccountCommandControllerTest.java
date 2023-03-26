package eu.whitebox.bank.controller;

import eu.whitebox.bank.command.CheckOverdraftCommand;
import eu.whitebox.bank.command.CreditCommand;
import eu.whitebox.bank.command.DebitCommand;
import eu.whitebox.bank.command.OpenAccountCommand;
import eu.whitebox.bank.common.TransactionType;
import eu.whitebox.bank.controller.view.CheckOverdraftRequest;
import eu.whitebox.bank.controller.view.OpenAccountRequest;
import eu.whitebox.bank.controller.view.TransactionRequest;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Ranjith
 */
public class AccountCommandControllerTest {

    private static final String BANK_QUERY_URL = "http://localhost:8080/bank-query";
    private CommandGateway commandGateway;
    private EventStore eventStore;
    private AccountCommandController controller;

    @Before
    public void setup() {
        commandGateway = Mockito.mock(CommandGateway.class);
        eventStore = Mockito.mock(EventStore.class);
        controller = new AccountCommandController(commandGateway, eventStore, BANK_QUERY_URL);
    }

    @Test
    public void testOpenAccount() throws Exception {
        final OpenAccountRequest request = new OpenAccountRequest("1234567890", new BigDecimal(100), new BigDecimal(500));
        final String expectedAccountId = request.getMobile();

        final ResponseEntity<URI> response = controller.openAccount(request);

        verify(commandGateway).send(new OpenAccountCommand(expectedAccountId, request.getInitialBalance(), request.getCreditLine()));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new URI(BANK_QUERY_URL + "/accounts/" + expectedAccountId), response.getHeaders().getLocation());
    }

    @Test
    public void testInitiateCreditTransaction() throws Exception {
        final TransactionRequest request = new TransactionRequest("1234567890", UUID.randomUUID().toString(), new BigDecimal(100), LocalDateTime.now(), TransactionType.CREDIT);

        final ResponseEntity<URI> response = controller.initiateTransaction(request);

        verify(commandGateway).send(new CreditCommand(request.getAccountId(), request.getTransactionId(), request.getAmount(), request.getTransactionDate()));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new URI(BANK_QUERY_URL + "/accounts/" + request.getAccountId()), response.getHeaders().getLocation());
    }

    @Test
    public void testInitiateDebitTransaction() throws Exception {
        final TransactionRequest request = new TransactionRequest("1234567890", UUID.randomUUID().toString(), new BigDecimal(100), LocalDateTime.now(), TransactionType.DEBIT);

        final ResponseEntity<URI> response = controller.initiateTransaction(request);

        verify(commandGateway).send(new DebitCommand(request.getAccountId(), request.getTransactionId(), request.getAmount(), request.getTransactionDate()));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new URI(BANK_QUERY_URL + "/accounts/" + request.getAccountId()), response.getHeaders().getLocation());
    }

    @Test
    public void testCheckOverDraft() {
        final CheckOverdraftRequest request = new CheckOverdraftRequest("1234567890", new BigDecimal(100));

        final ResponseEntity<String> response = controller.checkOverDraft(request);

        verify(commandGateway).send(new CheckOverdraftCommand(request.getAccountId(), request.getAmount()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Credit line not exceeded..", response.getBody());
    }
}