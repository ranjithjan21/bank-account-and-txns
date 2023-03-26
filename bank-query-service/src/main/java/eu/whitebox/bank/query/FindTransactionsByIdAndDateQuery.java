package eu.whitebox.bank.query;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * @author Ranjith
 */
@Value
public class FindTransactionsByIdAndDateQuery {

    String accountId;
    LocalDateTime fromDate;
}
