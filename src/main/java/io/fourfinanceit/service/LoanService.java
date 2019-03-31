package io.fourfinanceit.service;

import io.fourfinanceit.model.Loan;
import io.fourfinanceit.model.LoanStatus;
import io.fourfinanceit.model.User;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.validator.AmountValidator;
import io.fourfinanceit.validator.RiskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class LoanService {

    private static final Double NUMBER_OF_WEEKS = 52.18;
    private static final Double ROUND_OFF_MULTIPLIER = 100.00;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AmountValidator amountValidator;

    @Autowired
    RiskValidator riskValidator;

    @Value("${loan.application.max.count.per.ip}")
    private Integer loanApplicationMaxCountPerIp;

    @Value("${interest.rate}")
    private Double interestRate;

    @Value("${interest.rate.extension.factor.per.week}")
    private Double interestRateExtensionFactor;

    private double interestRatePerWeek;

    public LoanService() {
    }

    @PostConstruct
    public void init() {
        interestRatePerWeek = calculateRoundedInterestRate((interestRate / NUMBER_OF_WEEKS));
    }

    public List<Loan> findLoansByUser(Long id) {
        return loanRepository.findLoansByUser(id);
    }

    public Loan applyLoan(User user, Double amount, Integer term, String userIp) {
        riskValidator.analyzeLoanApplication(userIp);
        Loan loan = new Loan(user, amount, term, interestRatePerWeek);
        if (amountValidator.isValidLoanApplication(amount)) {
            if (riskValidator.isRiskAnalysisRequired(userIp, amount)) {
                loan.setStatus(LoanStatus.RISK_ANALYSIS_ON_HOLD);
            } else {
                loan.setStatus(LoanStatus.APPROVED);
            }
        } else {
            loan.setStatus(LoanStatus.REJECTED);
        }
        return loanRepository.save(loan);
    }

    public Loan findLoanById(Long loanId) {
        return loanRepository.findOne(loanId);
    }

    public Loan extendLoan(Long loanId, Integer term) {
        Loan loan = findLoanById(loanId);
        Integer currentTerm = loan.getTerm();
        Integer updatedTerm = currentTerm + term;
        Double currentInterestRate = loan.getInterestRatePerWeek();
        Double updatedInterestRate = calculateRoundedInterestRate((currentInterestRate * interestRateExtensionFactor));

        loan.setTerm(updatedTerm);
        loan.setInterestRatePerWeek(updatedInterestRate);
        loan.setExtended(Boolean.TRUE);
        return loanRepository.saveAndFlush(loan);
    }

    private Double calculateRoundedInterestRate(double interestRate) {
        return Math.round(interestRate * ROUND_OFF_MULTIPLIER) / ROUND_OFF_MULTIPLIER;
    }
}
