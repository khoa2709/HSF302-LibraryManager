package com.team5.librarymanager.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "BookId", nullable = false)
    private Book book;

    @Column(name = "LoanDate", nullable = false)
    private LocalDate loanDate;

    @Column(name = "DueDate", nullable = false)
    private LocalDate dueDate;

    @Column(name = "ReturnDate")
    private LocalDate returnDate;

    @Column(name = "Status", nullable = false)
    private String status;

    // Getters, setters, constructors
    public Loan() {}
    public Loan(User user, Book book, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 