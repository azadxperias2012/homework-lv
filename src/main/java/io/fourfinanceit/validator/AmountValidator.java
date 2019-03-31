package io.fourfinanceit.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmountValidator {

    @Value("${loan.amount.minimum}")
    private Double minimumLoanAmount;

    @Value("${loan.amount.maximum}")
    private Double maximumLoanAmount;

    public AmountValidator() {
    }

    public boolean isValidLoanApplication(Double amount) {
        return  (amount >= minimumLoanAmount && amount <= maximumLoanAmount);
    }

}
