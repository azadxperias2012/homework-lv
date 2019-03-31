package io.fourfinanceit.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RiskValidator {

    private static final Long ONE_DAY_IN_MILLI_SECONDS = 86400000L;

    @Value("${night.time.start}")
    private String nightStartTime;

    @Value("${night.time.end}")
    private String nightEndTime;

    @Value("${loan.amount.maximum}")
    private Double maximumLoanAmount;

    @Value("${loan.application.max.count.per.ip}")
    private Integer loanApplicationMaxCountPerIp;

    private ConcurrentHashMap<String, Queue<Long>> ipAndApplicationsPerDayMap;
    private LocalTime nightStartLocalTime;
    private LocalTime nightEndLocalTime;

    public RiskValidator() {
        ipAndApplicationsPerDayMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        nightStartLocalTime = LocalTime.parse(nightStartTime, timeFormatter);
        nightEndLocalTime = LocalTime.parse(nightEndTime, timeFormatter);
    }

    public void analyzeLoanApplication(String userIp) {
        ipAndApplicationsPerDayMap.putIfAbsent(userIp, new LinkedList<>());
        Queue<Long> loanApplicationCreatedTimeList = ipAndApplicationsPerDayMap.get(userIp);
        synchronized (loanApplicationCreatedTimeList) {
            long currentTime = new Date().getTime();
            loanApplicationCreatedTimeList.removeIf(loanApplicationTime -> isExpired(loanApplicationTime, currentTime));
            loanApplicationCreatedTimeList.add(currentTime);
        }
    }

    public boolean isRiskAnalysisRequired(String userIp, Double amount) {
        if ((!isWithinLoanApplicationLimitPerIp(userIp)) || (amount.equals(maximumLoanAmount) && isNightTime())) {
            return true;
        }
        return false;
    }

    private boolean isWithinLoanApplicationLimitPerIp(String userIp) {
        return ipAndApplicationsPerDayMap.get(userIp).size() <= loanApplicationMaxCountPerIp;
    }

    private boolean isExpired(Long loanApplicationTime, Long currentTime) {
        return loanApplicationTime < Long.sum(currentTime, -ONE_DAY_IN_MILLI_SECONDS);
    }

    private boolean isNightTime() {
        LocalTime now = LocalTime.now();
        return (now.isAfter(nightStartLocalTime) && now.isBefore(nightEndLocalTime));
    }

}
