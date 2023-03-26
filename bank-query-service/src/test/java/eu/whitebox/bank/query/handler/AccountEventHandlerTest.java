package eu.whitebox.bank.query.handler;

import eu.whitebox.bank.common.TransactionType;
import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import eu.whitebox.bank.data.repo.AccountRepository;
import eu.whitebox.bank.event.AccountCreditedEvent;
import eu.whitebox.bank.event.AccountDebitedEvent;
import eu.whitebox.bank.event.AccountOpenedEvent;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Ranjith
 */
public class AccountEventHandlerTest {

    private AccountRepository accountRepository;
    private AccountEventHandler accountEventHandler;

    @Before
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountEventHandler = new AccountEventHandler(accountRepository);
    }

    @Test
    public void testAccountOpenedEvent() {
        String accountId = "123";
        BigDecimal initialBalance = BigDecimal.ZERO;
        BigDecimal creditLine = BigDecimal.ZERO;
        AccountOpenedEvent event = new AccountOpenedEvent(accountId, initialBalance, creditLine);

        accountEventHandler.on(event);

        Account expectedAccount = new Account(accountId, initialBalance, creditLine, new ArrayList<>());
        verify(accountRepository).save(expectedAccount);
    }

    @Test
    public void testAccountCreditedEvent() {
        String accountId = "123";
        BigDecimal initialBalance = BigDecimal.ZERO;
        BigDecimal creditLine = BigDecimal.ZERO;
        Account account = new Account(accountId, initialBalance, creditLine, new ArrayList<>());
        BigDecimal amount = BigDecimal.TEN;
        String transactionId = "456";
        LocalDateTime transactionDate = LocalDateTime.now();
        AccountCreditedEvent event = new AccountCreditedEvent(accountId, transactionId, amount, transactionDate);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountEventHandler.on(event);

        BigDecimal expectedBalance = initialBalance.add(amount);
        Transaction expectedTransaction = new Transaction(transactionId, accountId, TransactionType.CREDIT, amount, transactionDate);
        account.setBalance(expectedBalance);
        account.getTransactions().add(expectedTransaction);
        verify(accountRepository).save(account);
    }

    @Test
    public void testAccountDebitedEvent() {
        String accountId = "123";
        BigDecimal initialBalance = BigDecimal.TEN;
        BigDecimal creditLine = BigDecimal.ZERO;
        Account account = new Account(accountId, initialBalance, creditLine, new ArrayList<>());
        BigDecimal amount = BigDecimal.ONE;
        String transactionId = "456";
        LocalDateTime transactionDate = LocalDateTime.now();
        AccountDebitedEvent event = new AccountDebitedEvent(accountId, transactionId, amount, transactionDate);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountEventHandler.on(event);

        BigDecimal expectedBalance = initialBalance.subtract(amount);
        Transaction expectedTransaction = new Transaction(transactionId, accountId, TransactionType.DEBIT, amount, transactionDate);
        account.setBalance(expectedBalance);
        account.getTransactions().add(expectedTransaction);
        verify(accountRepository).save(account);
    }
}
