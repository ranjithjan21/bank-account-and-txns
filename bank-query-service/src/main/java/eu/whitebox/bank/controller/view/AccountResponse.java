package eu.whitebox.bank.controller.view;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ranjith
 */
@Value
@JsonRootName("account")
public class AccountResponse {
    String accountId;
    BigDecimal balance;
    BigDecimal creditLine;
    List<TransactionResponse> transactions;
}
