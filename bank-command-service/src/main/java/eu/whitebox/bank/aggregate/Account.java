package eu.whitebox.bank.aggregate;

import eu.whitebox.bank.command.CheckOverdraftCommand;
import eu.whitebox.bank.command.CreditCommand;
import eu.whitebox.bank.command.DebitCommand;
import eu.whitebox.bank.command.OpenAccountCommand;
import eu.whitebox.bank.common.TransactionType;
import eu.whitebox.bank.event.AccountCreditedEvent;
import eu.whitebox.bank.event.AccountDebitedEvent;
import eu.whitebox.bank.event.AccountOpenedEvent;
import eu.whitebox.bank.exception.OverdraftLimitExceededException;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;


/**
 * @author Ranjith
 * <p>
 * This class represents a bank account and provides methods for opening, crediting, debiting, and checking overdraft.
 */
@Aggregate
@Getter
public class Account {

    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;
    private BigDecimal creditLine;
    private List<Transaction> transactions = new ArrayList<>();

    protected Account() {
    }

    /**
     * Creates an Account object with the given initial balance and credit line.
     *
     * @param command the command to open an account.
     */
    @CommandHandler
    public Account(final OpenAccountCommand command) {
        Assert.isTrue(BigDecimal.ZERO.compareTo(command.getInitialBalance()) < 0, "Invalid initial balance to open account");
        Assert.isTrue(BigDecimal.ZERO.compareTo(command.getCreditLine()) < 0, "Invalid credit line for opening account");
        apply(new AccountOpenedEvent(command.getAccountId(), command.getInitialBalance(), command.getCreditLine()));
    }

    /**
     * Credits the account with the given amount and creates a new transaction.
     *
     * @param command the command to credit the account.
     */
    @CommandHandler
    public void handle(final CreditCommand command) {
        Assert.isTrue(BigDecimal.ZERO.compareTo(command.getAmount()) < 0, "Invalid credit amount");
        apply(new AccountCreditedEvent(command.getAccountId(), command.getTransactionId(), command.getAmount(), command.getTransactionDate()));
    }

    /**
     * Debits the account with the given amount and creates a new transaction.
     *
     * @param command the command to debit the account.
     */
    @CommandHandler
    public void handle(final DebitCommand command) {
        Assert.isTrue(BigDecimal.ZERO.compareTo(command.getAmount()) < 0, "Invalid debit amount");
        final BigDecimal debitAmount = command.getAmount().negate(); // change the amount sign as negative if debit transaction.
        apply(new AccountDebitedEvent(accountId, command.getTransactionId(), debitAmount, command.getTransactionDate()));
    }


    /**
     * Checks if the account has sufficient balance to cover the given amount and credit line limit.
     *
     * @param command the command to check overdraft.
     */
    @CommandHandler
    public void handle(final CheckOverdraftCommand command) {
        Assert.isTrue(BigDecimal.ZERO.compareTo(command.getAmount()) < 0, "Invalid amount for checking overdraft");
        final BigDecimal debitAmount = command.getAmount().negate(); //change the amount sign to check overdraft
        final BigDecimal newBalance = balance.add(debitAmount);
        if (newBalance.compareTo(creditLine.negate()) < 0) {
            throw new OverdraftLimitExceededException("Sorry! Your overdraft limit exceeded and current balance is : " + balance);
        }
    }

    /**
     * Event handler for an AccountOpenedEvent, which sets the account's ID, initial balance, and credit line.
     *
     * @param event the event containing the account ID, initial balance, and credit line.
     */
    @EventSourcingHandler
    public void on(final AccountOpenedEvent event) {
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.creditLine = event.getCreditLine();
    }

    /**
     * Event handler for an AccountCreditedEvent, which adds the credited amount to the account's balance and adds a credit transaction to the list of transactions.
     *
     * @param event the event containing the account ID, credited amount, and transaction date.
     */
    @EventSourcingHandler
    public void on(final AccountCreditedEvent event) {
        this.balance = this.balance.add(event.getAmount());
        this.transactions.add(new Transaction(accountId, event.getTransactionId(), TransactionType.CREDIT, event.getAmount(), event.getTransactionDate()));
    }

    /**
     * Event handler for an AccountDebitedEvent, which subtracts the debited amount to the account's balance and adds a dedit transaction to the list of transactions.
     *
     * @param event the event containing the account ID, credited amount, and transaction date.
     */
    @EventSourcingHandler
    public void on(final AccountDebitedEvent event) {
        this.balance = this.balance.add(event.getAmount());
        this.transactions.add(new Transaction(accountId, event.getTransactionId(), TransactionType.DEBIT, event.getAmount(), event.getTransactionDate()));
    }
}
