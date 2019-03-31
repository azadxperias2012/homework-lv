package io.fourfinanceit.validator;

import io.fourfinanceit.HomeworkApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HomeworkApplication.class)
public class RiskValidatorTests {

    @Autowired
    RiskValidator riskValidator;

    @Test
    public void testIsRiskAnalysisRequired() {
        riskValidator.analyzeLoanApplication("localhost");
        riskValidator.analyzeLoanApplication("localhost");
        riskValidator.analyzeLoanApplication("localhost");
        riskValidator.analyzeLoanApplication("localhost");
        assertTrue(riskValidator.isRiskAnalysisRequired("localhost", 100000D));
    }

    @Test
    public void testIsRiskAnalysisNotRequired() {
        riskValidator.analyzeLoanApplication("localhost1");
        assertFalse(riskValidator.isRiskAnalysisRequired("localhost1", 100000D));
    }

}
