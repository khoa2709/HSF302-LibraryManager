package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.repository.BookRepository;
import com.team5.librarymanager.repository.LoanRepository;
import com.team5.librarymanager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final BookService bookService;
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    LoanService(BookService bookService) {
        this.bookService = bookService;
    }

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

    //Lấy lịch sử mượn sách của user
    public List<Loan> getLoanHistoryByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    //Thống kê sách đã mượn
    public Long countCurrentLoans() {
        return loanRepository.countByStatus(LoanStatus.BORROWED);
    }

    //Thống kê sách quá hạn
    public Long countOverdueLoans() {
        return loanRepository.countOverdueLoans();
    }

    //Thống kê số lượng sách
    public Long countByBookId(Long bookId) {
        return loanRepository.countByBookId(bookId);
    }
@Transactional
public void borrowBookAsStaff(Long bookId, LocalDate dueDate, String borrowerName) {
    Book book = bookService.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));

    if (!book.isStatus() || book.getQuantity() <= 0) {
        throw new IllegalStateException("Sách không khả dụng để mượn");
    }

    LocalDate today = LocalDate.now();
    LocalDate maxDueDate = today.plusDays(30);

    if (dueDate.isBefore(today) || dueDate.isAfter(maxDueDate)) {
        throw new IllegalStateException("Ngày trả không hợp lệ (tối đa 30 ngày)");
    }

    // Sinh username từ tên người mượn, loại bỏ khoảng trắng và chuyển về thường
    String baseUsername = borrowerName.trim().replaceAll("\\s+", "").toLowerCase();
    String username = baseUsername;
    int suffix = 1;
    while (userRepository.findByUsername(username) != null) {
        username = baseUsername + suffix;
        suffix++;
    }

    // Tạo user mới
    User borrower = new User();
    borrower.setFullName(borrowerName);
    borrower.setUsername(username);
    borrower.setPassword("123");
    borrower.setRole("member");
    borrower.setStatus(true);
    userRepository.save(borrower);

    // Tạo phiếu mượn mới
    Loan loan = new Loan();
    loan.setUser(borrower);
    loan.setBook(book);
    loan.setLoanDate(today);
    loan.setDueDate(dueDate);
    loan.setStatus(LoanStatus.BORROWED);

    loanRepository.save(loan);

    // Giảm số lượng sách
    book.setQuantity(book.getQuantity() - 1);
    bookRepository.save(book);
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

    private boolean isBookAvailable(Book book) {
        return book.isStatus() && book.getQuantity() > 0;
    }
    
}