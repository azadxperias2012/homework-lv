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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoanService {

    private static final Double NUMBER_OF_WEEKS = 52.18;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AmountValidator amountValidator;

    @Autowired
    RiskValidator riskValidator;

    @Value("${night.time.start}")
    private String nightStartTime;

    @Value("${night.time.end}")
    private String nightEndTime;

    @Value("${loan.amount.minimum}")
    private Double minimumLoanAmount;

    @Value("${loan.amount.maximum}")
    private Double maximumLoanAmount;

    @Value("${loan.application.max.count.per.ip}")
    private Integer loanApplicationMaxCountPerIp;

    @Value("${interest.rate}")
    private Double interestRate;

    @Value("${interest.rate.extension.factor.per.week}")
    private Double interestRateExtensionFactor;

    private double interestRatePerWeek;
    private ConcurrentHashMap<String, Queue<Long>> ipAndApplicationsPerDayMap;
    private LocalTime nightStartLocalTime;
    private LocalTime nightEndLocalTime;

    public LoanService() {
        ipAndApplicationsPerDayMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        interestRatePerWeek = calculateRoundedInterestRate((interestRate / NUMBER_OF_WEEKS));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        nightStartLocalTime = LocalTime.parse(nightStartTime, timeFormatter);
        nightEndLocalTime = LocalTime.parse(nightEndTime, timeFormatter);
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
        return Math.round(interestRate * 100.00) / 100.00;
    }
}
