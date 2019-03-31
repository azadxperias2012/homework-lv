package io.fourfinanceit.validator;

import io.fourfinanceit.HomeworkApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HomeworkApplication.class)
public class AmountValidatorTests {

    @Autowired
    AmountValidator amountValidator;

    @Test
    public void testIsValidLoanApplicationAmount() {
        assertTrue(amountValidator.isValidLoanApplication(50000D));
    }

    @Test
    public void testIsValidLoanApplicationMinimumAmount() {
        assertTrue(amountValidator.isValidLoanApplication(1000D));
    }

    @Test
    public void testIsValidLoanApplicationMaximumAmount() {
        assertTrue(amountValidator.isValidLoanApplication(100000D));
    }

    @Test
    public void testIsInValidLoanApplicationAmount() {
        assertFalse(amountValidator.isValidLoanApplication(500D));
        assertFalse(amountValidator.isValidLoanApplication(1000000D));
    }

}
