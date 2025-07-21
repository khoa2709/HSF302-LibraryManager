package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }

    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

    public Long count() {
        return loanRepository.count();
    }

    public void saveAll(List<Loan> list) {
        loanRepository.saveAll(list);
    }
}