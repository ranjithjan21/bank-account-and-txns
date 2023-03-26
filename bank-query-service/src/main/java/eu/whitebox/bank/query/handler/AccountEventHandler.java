package eu.whitebox.bank.query.handler;

import eu.whitebox.bank.common.TransactionType;
import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import eu.whitebox.bank.data.repo.AccountRepository;
import eu.whitebox.bank.event.AccountCreditedEvent;
import eu.whitebox.bank.event.AccountDebitedEvent;
import eu.whitebox.bank.event.AccountOpenedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Ranjith
 */
@ProcessingGroup("accounts")
@Service
public class AccountEventHandler {

    private final AccountRepository accountRepository;

    public AccountEventHandler(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Handles AccountOpenedEvent and creating a new Account object with the data provided in the event.
     * It then saves the newly created account to the account repository.
     *
     * @param event
     */
    @EventHandler
    public void on(AccountOpenedEvent event) {
        final Account account = new Account(event.getAccountId(), event.getInitialBalance(), event.getCreditLine(), new ArrayList<>());
        accountRepository.save(account);
    }

    /**
     * Handles AccountCreditedEvent and updating the corresponding account in the repository.
     * It retrieves the account from the repository using the account ID provided in the event, adds the credit amount to the account balance, and creates a new transaction with the credit details.
     * Finally, it saves the updated account to the repository.
     *
     * @param event
     */
    @EventHandler
    public void on(AccountCreditedEvent event) {
        final Account account = find(event.getAccountId());
        account.setBalance(account.getBalance().add(event.getAmount()));
        account.getTransactions().add(new Transaction(event.getTransactionId(), event.getAccountId(), TransactionType.CREDIT, event.getAmount(), event.getTransactionDate()));
        accountRepository.save(account);
    }

    /**
     * Handles AccountDebitedEvent and updating the corresponding account in the repository.
     * It retrieves the account from the repository using the account ID provided in the event, subtracts the debit amount to the account balance, and creates a new transaction with the credit details.
     * Finally, it saves the updated account to the repository.
     *
     * @param event
     */
    @EventHandler
    public void on(AccountDebitedEvent event) {
        final Account account = find(event.getAccountId());
        account.setBalance(account.getBalance().add(event.getAmount()));
        account.getTransactions().add(new Transaction(event.getTransactionId(), event.getAccountId(), TransactionType.DEBIT, event.getAmount(), event.getTransactionDate()));
        accountRepository.save(account);
    }

    /**
     * Retrieves an account from the repository using the provided account ID.
     * If no account is found with the provided ID, it throws an exception.
     *
     * @param accountId
     * @return account
     */
    private Account find(String accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalStateException("There is no bank account with account id " + accountId));
    }
}
