package eu.whitebox.bank.aggregate;

import eu.whitebox.bank.common.TransactionType;
import lombok.Value;
import org.axonframework.modelling.command.AggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Value
public class Transaction {
    @AggregateIdentifier
    String accountId;
    String transactionId;
    TransactionType transactionType;
    BigDecimal amount;
    LocalDateTime transactionDate;
}
