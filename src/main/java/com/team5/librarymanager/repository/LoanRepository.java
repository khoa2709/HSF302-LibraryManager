package com.team5.librarymanager.repository;

import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
    List<Loan> findByStatus(LoanStatus status);
} 