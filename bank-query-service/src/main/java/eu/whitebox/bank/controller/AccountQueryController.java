package eu.whitebox.bank.controller;

import eu.whitebox.bank.controller.view.AccountResponse;
import eu.whitebox.bank.controller.view.TransactionResponse;
import eu.whitebox.bank.query.FindAccountByIdQuery;
import eu.whitebox.bank.query.FindAccountsAreNegativeBalanceQuery;
import eu.whitebox.bank.query.FindAllAccountsQuery;
import eu.whitebox.bank.query.FindTransactionsByIdAndDateQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ranjith
 * <p>
 * REST API controller for retrieving accounts and its transactions.
 */
@RestController
@RequestMapping("/v1/bank-query/")
public class AccountQueryController {

    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    /**
     * Retrieves all transactions of an account from a specific date
     *
     * @param accountId
     * @param fromDate
     * @return transactions
     */
    @GetMapping("/account/transactions/{accountId}/{fromDate}")
    public List<TransactionResponse> getTransactionsSinceDate(@PathVariable final String accountId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime fromDate) {
        return queryGateway.query(new FindTransactionsByIdAndDateQuery(accountId, fromDate), ResponseTypes.multipleInstancesOf(TransactionResponse.class)).join();
    }

    /**
     * Retrieves all negative balance accounts
     *
     * @return accounts
     */
    @GetMapping("/accounts/negative-balance")
    public List<AccountResponse> getAccountsLessThanZeroBalance() {
        return queryGateway.query(new FindAccountsAreNegativeBalanceQuery(), ResponseTypes.multipleInstancesOf(AccountResponse.class)).join();
    }

    /**
     * Retrieves an account by accountId
     *
     * @param accountId
     * @return account
     */
    @GetMapping("/accounts/{accountId}")
    public AccountResponse getAccount(@PathVariable final String accountId) {
        return queryGateway.query(new FindAccountByIdQuery(accountId), AccountResponse.class).join();
    }

    /**
     * Retrieves all accounts
     *
     * @return account
     */
    @GetMapping("/accounts")
    public List<AccountResponse> getAllAccounts() {
        return queryGateway.query(new FindAllAccountsQuery(), ResponseTypes.multipleInstancesOf(AccountResponse.class)).join();
    }
}
