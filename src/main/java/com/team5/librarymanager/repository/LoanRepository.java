package com.team5.librarymanager.repository;

import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findAll();
    Long countByStatus(LoanStatus status); //(status = "BORROWED")
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'BORROWED' AND l.dueDate < CURRENT_DATE")
    Long countOverdueLoans();
    Long countByBookId(Long bookId);

    List<Loan> findAllByUser(User user);
}