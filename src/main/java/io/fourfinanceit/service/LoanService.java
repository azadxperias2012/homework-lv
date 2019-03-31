package io.fourfinanceit.service;

import io.fourfinanceit.model.Loan;
import io.fourfinanceit.model.LoanStatus;
import io.fourfinanceit.model.User;
import io.fourfinanceit.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static io.fourfinanceit.model.LoanStatus.EXTENDED;

@Service
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    @Autowired
    LoanRepository loanRepository;

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

    private LocalTime nightStartLocalTime;
    private LocalTime nightEndLocalTime;

    private ConcurrentHashMap<String, Queue<Long>> ipAndApplicationsPerDayMap;

    private static final Long ONE_DAY_IN_MILLI_SECONDS = 86400000L;
    private static final Double NUMBER_OF_WEEKS = 52.18;

    private double interestRatePerWeek;

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
        addLoanApplicationTimeAndRemoveExpired(userIp);
        Loan loan = new Loan(user, amount, term, interestRatePerWeek);
        if (isValidLoanApplication(amount)) {
            if (isRiskAnalysisRequired(userIp, amount)) {
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
        loan.setStatus(EXTENDED);
        return loanRepository.saveAndFlush(loan);
    }

    private void addLoanApplicationTimeAndRemoveExpired(String userIp) {
        ipAndApplicationsPerDayMap.putIfAbsent(userIp, new LinkedList<>());
        Queue<Long> loanApplicationCreatedTimeList = ipAndApplicationsPerDayMap.get(userIp);
        synchronized (loanApplicationCreatedTimeList) {
            long currentTime = new Date().getTime();
            loanApplicationCreatedTimeList.removeIf(loanApplicationTime -> isExpired(loanApplicationTime, currentTime));
            loanApplicationCreatedTimeList.add(currentTime);
        }
    }

    private boolean isValidLoanApplication(Double amount) {
        return  (amount >= minimumLoanAmount && amount <= maximumLoanAmount);
    }

    private boolean isRiskAnalysisRequired(String userIp, Double amount) {
        if ((!isWithinLoanApplicationLimitPerIp(userIp)) || (amount.equals(maximumLoanAmount) && isNightTime())) {
            return true;
        }
        return false;
    }

    private boolean isNightTime() {
        LocalTime now = LocalTime.now();
        return (now.isAfter(nightStartLocalTime) && now.isBefore(nightEndLocalTime));
    }

    private boolean isWithinLoanApplicationLimitPerIp(String userIp) {
        return ipAndApplicationsPerDayMap.get(userIp).size() <= loanApplicationMaxCountPerIp;
    }

    private boolean isExpired(Long loanApplicationTime, Long currentTime) {
        return loanApplicationTime < Long.sum(currentTime, -ONE_DAY_IN_MILLI_SECONDS);
    }

    private Double calculateRoundedInterestRate(double interestRate) {
        return Math.round(interestRate * 100.00) / 100.00;
    }
}
