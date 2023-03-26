package eu.whitebox.bank.controller.view;

import com.fasterxml.jackson.annotation.JsonRootName;
import eu.whitebox.bank.common.TransactionType;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Value
@JsonRootName("transaction")
public class TransactionResponse {
    String transactionId;
    TransactionType transactionType;
    BigDecimal amount;
    LocalDateTime transactionDate;
}
