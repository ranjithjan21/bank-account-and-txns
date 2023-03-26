package eu.whitebox.bank.query.handler;

import eu.whitebox.bank.controller.view.AccountResponse;
import eu.whitebox.bank.controller.view.TransactionResponse;
import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import eu.whitebox.bank.data.repo.AccountRepository;
import eu.whitebox.bank.query.FindAccountByIdQuery;
import eu.whitebox.bank.query.FindAccountsAreNegativeBalanceQuery;
import eu.whitebox.bank.query.FindAllAccountsQuery;
import eu.whitebox.bank.query.FindTransactionsByIdAndDateQuery;
import eu.whitebox.bank.query.transform.QueryResponseTransformer;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author Ranjith
 */
@Service
public class AccountQueryHandler {

    private final AccountRepository accountRepository;
    private final QueryResponseTransformer queryResponseTransformer;

    public AccountQueryHandler(AccountRepository accountRepository, QueryResponseTransformer queryResponseTransformer) {
        this.accountRepository = accountRepository;
        this.queryResponseTransformer = queryResponseTransformer;
    }

    /**
     * Handles FindAllAccountsQuery and retrieves all the accounts.
     *
     * @param query
     * @return accounts
     */
    @QueryHandler
    public List<AccountResponse> handle(FindAllAccountsQuery query) {
        return queryResponseTransformer.buildAccountResponses(accountRepository.findAll());
    }

    /**
     * Handles FindAccountByIdQuery and retrieves an account.
     *
     * @param query
     * @return account
     */
    @QueryHandler
    public AccountResponse handle(FindAccountByIdQuery query) {
        final Account account = accountRepository.findById(query.getAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid account Id"));

        return queryResponseTransformer.buildAccountResponse(account);
    }

    /**
     * Handles FindTransactionsByIdAndDateQuery and retrieves the transactions from a specific date.
     *
     * @param query
     * @return transactions
     */
    @QueryHandler
    public List<TransactionResponse> handle(FindTransactionsByIdAndDateQuery query) {
        final List<Transaction> transactions = accountRepository.findTransactionsByAccountIdAndFromDate(query.getAccountId(), query.getFromDate());
        return queryResponseTransformer.buildTransactionResponses(transactions);
    }

    /**
     * Handles FindAccountsAreNegativeBalanceQuery and retrieves the negative balance accounts
     *
     * @param query
     * @return accounts
     */
    @QueryHandler
    public List<AccountResponse> handle(FindAccountsAreNegativeBalanceQuery query) {
        final List<Account> accounts = accountRepository.findAccountsByBalanceLessThan(BigDecimal.ZERO);
        return queryResponseTransformer.buildAccountResponses(accounts);
    }
}
