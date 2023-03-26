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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ranjith
 * <p>
 * REST API controller for account-related commands, with endpoints for opening an account, initiating a transaction,
 * checking for overdrafts, and reading events associated with an account.
 */
@RestController
@RequestMapping("/v1/bank-command/")
public class AccountCommandController {

    private String bankQueryUrl;
    private final CommandGateway commandGateway;
    private final EventStore eventStore;

    /**
     * Constructor for the controller class, which takes in a CommandGateway and EventStore as parameters.
     *
     * @param commandGateway - A CommandGateway instance used to send commands to the account aggregate.
     * @param eventStore     - An EventStore instance used to read events associated with an account.
     * @param bankQueryUrl   - A String representing the URL of the bank query API.
     */
    public AccountCommandController(final CommandGateway commandGateway,
                                    final EventStore eventStore,
                                    @Value("${bank.query.url}") final String bankQueryUrl) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.bankQueryUrl = bankQueryUrl;
    }


    /**
     * REST API endpoint for opening an account, which sends an OpenAccountCommand to the account aggregate.
     *
     * @param request - An OpenAccountRequest object containing the account opening details.
     * @return - A ResponseEntity object containing the URL of the newly opened account.
     * @throws URISyntaxException - An exception thrown if there is an issue with creating the account URL.
     */
    @PutMapping("/open-account")
    public ResponseEntity<URI> openAccount(@RequestBody OpenAccountRequest request) throws URISyntaxException {
        final String accountId = request.getMobile(); //for simplicity, using mobile number as accountId to maintain uniqueness
        commandGateway.send(new OpenAccountCommand(accountId, request.getInitialBalance(), request.getCreditLine()));
        return ResponseEntity
            .created(new URI(bankQueryUrl + "/accounts/" + accountId))
            .build();
    }

    /**
     * REST API endpoint for initiating a transaction, which sends a CreditCommand or DebitCommand to the account aggregate.
     *
     * @param request - A TransactionRequest object containing the transaction details.
     * @return - A ResponseEntity object containing the URL of the account associated with the transaction.
     * @throws URISyntaxException - An exception thrown if there is an issue with creating the account URL.
     */
    @PutMapping("/account/transaction")
    public ResponseEntity<URI> initiateTransaction(@RequestBody TransactionRequest request) throws URISyntaxException {
        Assert.isTrue(Arrays.stream(TransactionType.values()).anyMatch(type -> type == request.getTransactionType()), "Invalid Transaction Type");

        if (TransactionType.CREDIT == request.getTransactionType()) {
            commandGateway.send(new CreditCommand(request.getAccountId(), request.getTransactionId(), request.getAmount(), request.getTransactionDate()));
        }
        if (TransactionType.DEBIT == request.getTransactionType()) {
            commandGateway.send(new DebitCommand(request.getAccountId(), request.getTransactionId(), request.getAmount(), request.getTransactionDate()));
        }

        return ResponseEntity
            .created(new URI(bankQueryUrl + "/accounts/" + request.getAccountId()))
            .build();
    }

    /**
     * REST API endpoint for checking for overdrafts, which sends a CheckOverdraftCommand to the account aggregate.
     *
     * @param request - A CheckOverdraftRequest object containing the account and overdraft amount details.
     * @return - A ResponseEntity object containing a message indicating whether the credit line has been exceeded.
     */
    @PostMapping("account/check-overdraft")
    public ResponseEntity<String> checkOverDraft(@RequestBody CheckOverdraftRequest request) {
        commandGateway.send(new CheckOverdraftCommand(request.getAccountId(), request.getAmount()));
        return ResponseEntity.ok("Credit line not exceeded..");
    }

    /**
     * Retrieves a list of events for a specified account by calling the readEvents() method of the eventStore object.
     *
     * @param accountId
     * @return events
     */
    @GetMapping("/events/{accountId}")
    public List<String> readEvents(@PathVariable final String accountId) {
        return eventStore.readEvents(accountId)
            .asStream()
            .map(Object::toString)
            .collect(Collectors.toList());
    }
}