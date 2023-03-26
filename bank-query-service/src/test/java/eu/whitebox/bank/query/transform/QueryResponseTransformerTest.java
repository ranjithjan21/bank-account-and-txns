package eu.whitebox.bank.query.transform;

import eu.whitebox.bank.common.TransactionType;
import eu.whitebox.bank.controller.view.AccountResponse;
import eu.whitebox.bank.controller.view.TransactionResponse;
import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ranjith
 */
public class QueryResponseTransformerTest {

    private QueryResponseTransformer queryResponseTransformer = new QueryResponseTransformer();

    @Test
    public void testBuildAccountResponses() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("1", new BigDecimal("100"), new BigDecimal("500"), new ArrayList<>()));
        accounts.add(new Account("2", new BigDecimal("200"), new BigDecimal("1000"), new ArrayList<>()));

        List<AccountResponse> accountResponses = queryResponseTransformer.buildAccountResponses(accounts);

        assertEquals(accounts.size(), accountResponses.size());
        assertEquals(accounts.get(0).getAccountId(), accountResponses.get(0).getAccountId());
        assertEquals(accounts.get(0).getBalance(), accountResponses.get(0).getBalance());
        assertEquals(accounts.get(0).getCreditLine(), accountResponses.get(0).getCreditLine());
        assertEquals(accounts.get(0).getTransactions().size(), accountResponses.get(0).getTransactions().size());
    }

    @Test
    public void testBuildAccountResponse() {
        Account account = new Account("1", new BigDecimal("100"), new BigDecimal("500"), new ArrayList<>());

        AccountResponse accountResponse = queryResponseTransformer.buildAccountResponse(account);

        assertEquals(account.getAccountId(), accountResponse.getAccountId());
        assertEquals(account.getBalance(), accountResponse.getBalance());
        assertEquals(account.getCreditLine(), accountResponse.getCreditLine());
        assertEquals(account.getTransactions().size(), accountResponse.getTransactions().size());
    }

    @Test
    public void testBuildTransactionResponses() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "1", TransactionType.CREDIT, new BigDecimal("100"), LocalDateTime.now()));
        transactions.add(new Transaction("2", "1", TransactionType.DEBIT, new BigDecimal("50"), LocalDateTime.now()));

        List<TransactionResponse> transactionResponses = queryResponseTransformer.buildTransactionResponses(transactions);

        assertEquals(transactions.size(), transactionResponses.size());
        assertEquals(transactions.get(0).getTransactionId(), transactionResponses.get(0).getTransactionId());
        assertEquals(transactions.get(0).getTransactionType(), transactionResponses.get(0).getTransactionType());
        assertEquals(transactions.get(0).getAmount(), transactionResponses.get(0).getAmount());
        assertEquals(transactions.get(0).getTransactionDate(), transactionResponses.get(0).getTransactionDate());
    }

    @Test
    public void testBuildTransactionResponse() {
        Transaction transaction = new Transaction("1", "1", TransactionType.CREDIT, new BigDecimal("100"), LocalDateTime.now());

        TransactionResponse transactionResponse = queryResponseTransformer.buildTransactionResponse(transaction);

        assertEquals(transaction.getTransactionId(), transactionResponse.getTransactionId());
        assertEquals(transaction.getTransactionType(), transactionResponse.getTransactionType());
        assertEquals(transaction.getAmount(), transactionResponse.getAmount());
        assertEquals(transaction.getTransactionDate(), transactionResponse.getTransactionDate());
    }
}
