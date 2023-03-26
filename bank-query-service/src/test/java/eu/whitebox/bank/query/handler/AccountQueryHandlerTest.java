package eu.whitebox.bank.query.handler;

import eu.whitebox.bank.common.TransactionType;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ranjith
 */
public class AccountQueryHandlerTest {

    private AccountRepository accountRepository;
    private QueryResponseTransformer queryResponseTransformer;
    private AccountQueryHandler accountQueryHandler;

    @Before
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        queryResponseTransformer = mock(QueryResponseTransformer.class);
        accountQueryHandler = new AccountQueryHandler(accountRepository, queryResponseTransformer);
    }

    @Test
    public void testFindAllAccountsQuery() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("1", BigDecimal.valueOf(100), BigDecimal.ZERO, new ArrayList<>()));
        accounts.add(new Account("2", BigDecimal.valueOf(200), BigDecimal.ZERO, new ArrayList<>()));
        when(accountRepository.findAll()).thenReturn(accounts);

        List<AccountResponse> accountResponses = new ArrayList<>();
        accountResponses.add(new AccountResponse("1", BigDecimal.valueOf(100), BigDecimal.ZERO, new ArrayList<>()));
        accountResponses.add(new AccountResponse("2", BigDecimal.valueOf(200), BigDecimal.ZERO, new ArrayList<>()));
        when(queryResponseTransformer.buildAccountResponses(accounts)).thenReturn(accountResponses);

        FindAllAccountsQuery query = new FindAllAccountsQuery();
        List<AccountResponse> result = accountQueryHandler.handle(query);

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("1", result.get(0).getAccountId());
        Assert.assertEquals(BigDecimal.valueOf(100), result.get(0).getBalance());
        Assert.assertEquals(BigDecimal.ZERO, result.get(0).getCreditLine());
        Assert.assertEquals(0, result.get(0).getTransactions().size());
        Assert.assertEquals("2", result.get(1).getAccountId());
        Assert.assertEquals(BigDecimal.valueOf(200), result.get(1).getBalance());
        Assert.assertEquals(BigDecimal.ZERO, result.get(1).getCreditLine());
        Assert.assertEquals(0, result.get(1).getTransactions().size());
    }

    @Test
    public void testFindAccountByIdQuery() {
        Account account = new Account("1", BigDecimal.valueOf(100), BigDecimal.ZERO, new ArrayList<>());
        when(accountRepository.findById("1")).thenReturn(Optional.of(account));

        AccountResponse accountResponse = new AccountResponse("1", BigDecimal.valueOf(100), BigDecimal.ZERO, new ArrayList<>());
        when(queryResponseTransformer.buildAccountResponse(account)).thenReturn(accountResponse);

        FindAccountByIdQuery query = new FindAccountByIdQuery("1");
        AccountResponse result = accountQueryHandler.handle(query);

        Assert.assertEquals("1", result.getAccountId());
        Assert.assertEquals(BigDecimal.valueOf(100), result.getBalance());
        Assert.assertEquals(BigDecimal.ZERO, result.getCreditLine());
        Assert.assertEquals(0, result.getTransactions().size());
    }


    @Test
    public void testFindAccountsLessThanZeroBalanceQuery() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("1", BigDecimal.valueOf(-50), BigDecimal.ZERO, new ArrayList<>()));
        accounts.add(new Account("2", BigDecimal.valueOf(200), BigDecimal.ZERO, new ArrayList<>()));
        when(accountRepository.findAccountsByBalanceLessThan(BigDecimal.ZERO)).thenReturn(accounts);

        List<AccountResponse> accountResponses = new ArrayList<>();
        accountResponses.add(new AccountResponse("1", BigDecimal.valueOf(-50), BigDecimal.ZERO, new ArrayList<>()));
        when(queryResponseTransformer.buildAccountResponses(accounts)).thenReturn(accountResponses);

        FindAccountsAreNegativeBalanceQuery query = new FindAccountsAreNegativeBalanceQuery();
        List<AccountResponse> result = accountQueryHandler.handle(query);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("1", result.get(0).getAccountId());
        Assert.assertEquals(BigDecimal.valueOf(-50), result.get(0).getBalance());
        Assert.assertEquals(BigDecimal.ZERO, result.get(0).getCreditLine());
        Assert.assertEquals(0, result.get(0).getTransactions().size());
    }

    @Test
    public void testFindTransactionsByIdAndDateQuery() {
        String accountId = "1";
        LocalDateTime startDate = LocalDateTime.of(2023, 3, 1, 0, 0);

        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction("1", accountId, TransactionType.CREDIT, BigDecimal.valueOf(100), LocalDateTime.of(2023, 3, 5, 12, 0));
        Transaction transaction2 = new Transaction("2", accountId, TransactionType.DEBIT, BigDecimal.valueOf(50), LocalDateTime.of(2023, 3, 10, 15, 30));
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(accountRepository.findTransactionsByAccountIdAndFromDate(Mockito.anyString(), Mockito.any())).thenReturn(transactions);

        List<TransactionResponse> transactionResponses = new ArrayList<>();
        TransactionResponse transactionResponse1 = new TransactionResponse("1", TransactionType.CREDIT, BigDecimal.valueOf(100), LocalDateTime.of(2023, 3, 5, 12, 0));
        TransactionResponse transactionResponse2 = new TransactionResponse("2", TransactionType.DEBIT, BigDecimal.valueOf(50), LocalDateTime.of(2023, 3, 10, 15, 30));
        transactionResponses.add(transactionResponse1);
        transactionResponses.add(transactionResponse2);
        when(queryResponseTransformer.buildTransactionResponses(transactions)).thenReturn(transactionResponses);

        FindTransactionsByIdAndDateQuery query = new FindTransactionsByIdAndDateQuery(accountId, startDate);
        List<TransactionResponse> result = accountQueryHandler.handle(query);

        Assert.assertEquals(2, result.size());

        Assert.assertEquals("1", result.get(0).getTransactionId());
        Assert.assertEquals(TransactionType.CREDIT, result.get(0).getTransactionType());
        Assert.assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
        Assert.assertEquals(LocalDateTime.of(2023, 3, 5, 12, 0), result.get(0).getTransactionDate());

        Assert.assertEquals("2", result.get(1).getTransactionId());
        Assert.assertEquals(TransactionType.DEBIT, result.get(1).getTransactionType());
        Assert.assertEquals(BigDecimal.valueOf(50), result.get(1).getAmount());
        Assert.assertEquals(LocalDateTime.of(2023, 3, 10, 15, 30), result.get(1).getTransactionDate());
    }
}