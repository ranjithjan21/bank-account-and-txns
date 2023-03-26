package eu.whitebox.bank.controller.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Ranjith
 */
@Data
@AllArgsConstructor
public class CheckOverdraftRequest {

    private String accountId;
    private BigDecimal amount;
}
