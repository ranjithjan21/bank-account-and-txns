package eu.whitebox.bank.controller;


import eu.whitebox.bank.controller.view.AccountResponse;
import eu.whitebox.bank.controller.view.TransactionResponse;
import eu.whitebox.bank.query.FindAccountsAreNegativeBalanceQuery;
import eu.whitebox.bank.query.FindAllAccountsQuery;
import eu.whitebox.bank.query.FindTransactionsByIdAndDateQuery;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Ranjith
 */
public class AccountQueryControllerTest {

    private QueryGateway queryGateway;

    private AccountQueryController accountQueryController;

    @Before
    public void setup() {
        queryGateway = Mockito.mock(QueryGateway.class);
        accountQueryController = new AccountQueryController(queryGateway);
    }

    @Test
    public void testGetTransactionsSinceDate() {
        String accountId = "12345";
        LocalDateTime fromDate = LocalDateTime.now();
        List<TransactionResponse> transactions = new ArrayList<>();
        when(queryGateway.query(any(FindTransactionsByIdAndDateQuery.class), any(ResponseType.class)))
            .thenReturn(CompletableFuture.completedFuture(transactions));

        List<TransactionResponse> result = accountQueryController.getTransactionsSinceDate(accountId, fromDate);

        verify(queryGateway, times(1)).query(any(FindTransactionsByIdAndDateQuery.class), any(ResponseType.class));
        assertEquals(transactions, result);
    }

    @Test
    public void testGetAccountsLessThanZeroBalance() {
        List<AccountResponse> accounts = new ArrayList<>();
        when(queryGateway.query(any(FindAccountsAreNegativeBalanceQuery.class), any(ResponseType.class)))
            .thenReturn(CompletableFuture.completedFuture(accounts));

        List<AccountResponse> result = accountQueryController.getAccountsLessThanZeroBalance();

        verify(queryGateway, times(1)).query(any(FindAccountsAreNegativeBalanceQuery.class), any(ResponseType.class));
        assertEquals(accounts, result);
    }

    @Test
    public void testGetAllAccounts() {
        List<AccountResponse> accounts = new ArrayList<>();
        when(queryGateway.query(any(FindAllAccountsQuery.class), any(ResponseType.class)))
            .thenReturn(CompletableFuture.completedFuture(accounts));

        List<AccountResponse> result = accountQueryController.getAllAccounts();

        verify(queryGateway, times(1)).query(any(FindAllAccountsQuery.class), any(ResponseType.class));
        assertEquals(accounts, result);
    }
}
