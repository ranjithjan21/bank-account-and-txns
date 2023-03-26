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
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ranjith
 */
public class AccountTest {

    private static final String ACCOUNT_ID = "ACC-123456";
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);
    private static final BigDecimal CREDIT_LINE = BigDecimal.valueOf(500);
    private static final String TRANSACTION_ID = UUID.randomUUID().toString();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(200);
    private static final LocalDateTime TRANSACTION_DATE = LocalDateTime.now();

    private AggregateTestFixture<Account> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    public void testOpenAccount() {
        fixture.givenNoPriorActivity()
            .when(new OpenAccountCommand(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .expectEvents(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .expectState(account -> {
                assertEquals(ACCOUNT_ID, account.getAccountId());
                assertEquals(INITIAL_BALANCE, account.getBalance());
                assertEquals(CREDIT_LINE, account.getCreditLine());
                assertTrue(account.getTransactions().isEmpty());
            });
    }

    @Test
    public void testCreditAccount() {
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .when(new CreditCommand(ACCOUNT_ID, TRANSACTION_ID, AMOUNT, TRANSACTION_DATE))
            .expectEvents(new AccountCreditedEvent(ACCOUNT_ID, TRANSACTION_ID, AMOUNT, TRANSACTION_DATE))
            .expectState(account -> {
                assertEquals(ACCOUNT_ID, account.getAccountId());
                assertEquals(INITIAL_BALANCE.add(AMOUNT), account.getBalance());
                assertEquals(CREDIT_LINE, account.getCreditLine());
                assertEquals(1, account.getTransactions().size());
                assertEquals(TransactionType.CREDIT, account.getTransactions().get(0).getTransactionType());
                assertEquals(AMOUNT, account.getTransactions().get(0).getAmount());
                assertEquals(TRANSACTION_DATE, account.getTransactions().get(0).getTransactionDate());
            });
    }

    @Test
    public void testDebitAccount() {
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .when(new DebitCommand(ACCOUNT_ID, TRANSACTION_ID, AMOUNT, TRANSACTION_DATE))
            .expectEvents(new AccountDebitedEvent(ACCOUNT_ID, TRANSACTION_ID, AMOUNT.negate(), TRANSACTION_DATE))
            .expectState(account -> {
                assertEquals(ACCOUNT_ID, account.getAccountId());
                assertEquals(INITIAL_BALANCE.subtract(AMOUNT), account.getBalance());
                assertEquals(CREDIT_LINE, account.getCreditLine());
                assertEquals(1, account.getTransactions().size());
                assertEquals(TransactionType.DEBIT, account.getTransactions().get(0).getTransactionType());
                assertEquals(AMOUNT.negate(), account.getTransactions().get(0).getAmount());
                assertEquals(TRANSACTION_DATE, account.getTransactions().get(0).getTransactionDate());
            });
    }

    @Test
    public void testThrowOverdraftLimitExceededException() {
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, BigDecimal.valueOf(1000).negate(), CREDIT_LINE))
            .when(new CheckOverdraftCommand(ACCOUNT_ID, AMOUNT))
            .expectException(OverdraftLimitExceededException.class);
    }

    @Test
    public void testNotOpenAccountWithNegativeInitialBalance() {
        BigDecimal initialBalance = BigDecimal.valueOf(-1000);
        fixture.givenNoPriorActivity()
            .when(new OpenAccountCommand(ACCOUNT_ID, initialBalance, CREDIT_LINE))
            .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testNotHandleDebitCommandWithNegativeAmount() {
        BigDecimal amount = BigDecimal.valueOf(-200);
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .when(new DebitCommand(ACCOUNT_ID, TRANSACTION_ID, amount, TRANSACTION_DATE))
            .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testNotHandleCreditCommandWithNegativeAmount() {
        BigDecimal amount = BigDecimal.valueOf(-200);
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .when(new CreditCommand(ACCOUNT_ID, TRANSACTION_ID, amount, LocalDateTime.now()))
            .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testNotHandleCheckOverdraftCommandWithNegativeAmount() {
        BigDecimal amount = BigDecimal.valueOf(-200);
        fixture.given(new AccountOpenedEvent(ACCOUNT_ID, INITIAL_BALANCE, CREDIT_LINE))
            .when(new CheckOverdraftCommand(ACCOUNT_ID, amount))
            .expectException(IllegalArgumentException.class);
    }
}