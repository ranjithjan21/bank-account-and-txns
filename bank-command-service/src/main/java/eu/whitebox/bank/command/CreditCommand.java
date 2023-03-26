package eu.whitebox.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Value
public class CreditCommand {

    @TargetAggregateIdentifier
    String accountId;
    String transactionId;
    BigDecimal amount;
    LocalDateTime transactionDate;
}
