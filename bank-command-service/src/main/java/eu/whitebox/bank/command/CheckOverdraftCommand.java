package eu.whitebox.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

/**
 * @author Ranjith
 */
@Value
public class CheckOverdraftCommand {

    @TargetAggregateIdentifier
    String accountId;
    BigDecimal amount;
}
