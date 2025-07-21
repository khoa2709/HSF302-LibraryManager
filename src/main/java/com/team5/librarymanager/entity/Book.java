package com.team5.librarymanager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(name = "Author", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String author;

    @Column(name = "Publisher", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String publisher;

    @Column(name = "Year", nullable = false)
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Loan> loans;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters, setters, constructors
    public Book() {}
    public Book(String title, String author, String publisher, Integer year, Category category, Integer quantity) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.category = category;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 