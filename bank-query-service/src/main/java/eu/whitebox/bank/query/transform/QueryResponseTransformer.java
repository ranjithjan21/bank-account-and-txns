package eu.whitebox.bank.query.transform;

import eu.whitebox.bank.controller.view.AccountResponse;
import eu.whitebox.bank.controller.view.TransactionResponse;
import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RanjithF
 */
@Component
public class QueryResponseTransformer {

    public List<AccountResponse> buildAccountResponses(final List<Account> accounts) {
        return accounts
            .stream()
            .map(this::buildAccountResponse)
            .collect(Collectors.toList());
    }

    public AccountResponse buildAccountResponse(final Account account) {
        return new AccountResponse(account.getAccountId(), account.getBalance(), account.getCreditLine(), buildTransactionResponses(account.getTransactions()));
    }

    public List<TransactionResponse> buildTransactionResponses(final List<Transaction> transactions) {
        return transactions
            .stream()
            .map(this::buildTransactionResponse)
            .collect(Collectors.toList());
    }

    public TransactionResponse buildTransactionResponse(final Transaction txn) {
        return new TransactionResponse(txn.getTransactionId(), txn.getTransactionType(), txn.getAmount(), txn.getTransactionDate());
    }
}
