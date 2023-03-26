package eu.whitebox.bank.event;

import lombok.Value;

import java.math.BigDecimal;

/**
 * @author Ranjith
 */
@Value
public class AccountOpenedEvent {
    String accountId;
    BigDecimal initialBalance;
    BigDecimal creditLine;
}
