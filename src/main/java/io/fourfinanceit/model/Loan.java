package io.fourfinanceit.model;

import javax.persistence.*;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Double amount;
    Integer term;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    User user;

    public Loan(Double amount, Integer term, User user) {
        this.amount = amount;
        this.term = term;
        this.user = user;
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

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", amount=" + amount +
                ", term=" + term +
                ", user=" + user +
                '}';
    }

}
