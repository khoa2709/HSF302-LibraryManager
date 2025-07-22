package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.repository.BookRepository;
import com.team5.librarymanager.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    // Các phương thức cơ bản
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public void saveAll(List<Loan> list) {
        loanRepository.saveAll(list);
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }

    // Các phương thức tìm kiếm
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getLoansByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public List<Loan> getCurrentLoans() {
        return loanRepository.findByStatus(LoanStatus.BORROWED);
    }

    public List<Loan> getLoansByUserAndStatus(Long userId, LoanStatus status) {
        return loanRepository.findByUserIdAndStatus(userId, status);
    }

    @Transactional
    public Loan borrowBook(User user, Book book, int loanDays) {
        // Kiểm tra sách có sẵn không
        if (!isBookAvailable(book)) {
            throw new IllegalStateException("Sách không khả dụng để mượn");
        }

        // Tạo phiếu mượn mới
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(loanDays);
        
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.BORROWED);

        // Cập nhật số lượng sách
        book.setQuantity(book.getQuantity() - 1);
        if (book.getQuantity() == 0) {
            book.setStatus(false);
        }
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

   //Trả sách
    @Transactional
    public void returnBook(Loan loan) {
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        
        // Tăng số lượng sách khi trả
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);
        
        loanRepository.save(loan);
    }

    /**
     * Kiểm tra sách có khả dụng không
     * @param book Sách cần kiểm tra
     * @return true nếu sách khả dụng, false nếu không
     */
    private boolean isBookAvailable(Book book) {
        return book.isStatus() && book.getQuantity() > 0;
    }
}