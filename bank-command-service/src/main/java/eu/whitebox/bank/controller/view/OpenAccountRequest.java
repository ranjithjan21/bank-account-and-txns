package eu.whitebox.bank.controller.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Ranjith
 */
@Data
@AllArgsConstructor @NoArgsConstructor
public class OpenAccountRequest {

    private String mobile;
    private BigDecimal initialBalance;
    private BigDecimal creditLine;
}
