package eu.whitebox.bank.controller.view;

import eu.whitebox.bank.common.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Data
@AllArgsConstructor
public class TransactionRequest {

    private String accountId;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private TransactionType transactionType;
}
