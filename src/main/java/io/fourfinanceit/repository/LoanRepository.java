package io.fourfinanceit.repository;

import io.fourfinanceit.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select l from Loan l where l.user.id = :userId")
    List<Loan> findLoansByUser(@Param("userId") Long id);

}
