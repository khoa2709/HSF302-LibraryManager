package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LoanReminderScheduler {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private EmailService emailService;

    // Chạy lúc 7h sáng mỗi ngày
    @Scheduled(cron = "0 0 7 * * *")
    public void sendReturnReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Loan> loans = loanRepository.findByStatus(LoanStatus.BORROWED);
        for (Loan loan : loans) {
            if (loan.getDueDate() != null && loan.getDueDate().isEqual(tomorrow)) {
                if (loan.getUser() != null && loan.getUser().getEmail() != null && !loan.getUser().getEmail().isBlank()) {
                    emailService.sendReturnReminderMail(
                        loan.getUser().getEmail(),
                        loan.getBook().getTitle(),
                        loan.getDueDate()
                    );
                }
            }
        }
    }
}
