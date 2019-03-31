package io.fourfinanceit.model;

import javax.persistence.*;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Double amount;
    Integer term;
    Double interestRatePerWeek;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    User user;

    LoanStatus status;

    public Loan() {
    }

    public Loan(User user, Double amount, Integer term, Double interestRatePerWeek) {
        this(user, amount, term, interestRatePerWeek, LoanStatus.APPROVED);
    }

    public Loan(User user, Double amount, Integer term, Double interestRatePerWeek, LoanStatus loanStatus) {
        this.user = user;
        this.amount = amount;
        this.term = term;
        this.interestRatePerWeek = interestRatePerWeek;
        this.status = loanStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Double getInterestRatePerWeek() {
        return interestRatePerWeek;
    }

    public void setInterestRatePerWeek(Double interestRatePerWeek) {
        this.interestRatePerWeek = interestRatePerWeek;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", amount=" + amount +
                ", term=" + term +
                ", interestRatePerWeek=" + interestRatePerWeek +
                ", user=" + user +
                ", status=" + status +
                '}';
    }
}
