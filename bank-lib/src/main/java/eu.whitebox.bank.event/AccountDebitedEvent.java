package eu.whitebox.bank.event;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Value
public class AccountDebitedEvent {
    String accountId;
    String transactionId;
    BigDecimal amount;
    LocalDateTime transactionDate;
}
