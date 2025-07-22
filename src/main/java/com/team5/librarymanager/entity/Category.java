package com.team5.librarymanager.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "Description", columnDefinition = "NVARCHAR(2000)")
    private String description;

    @Column(name = "Status", nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Book> books;

    // Getters, setters, constructors
    public Category() {}
    public Category(String name, String description, boolean status) {
        this.name = name;
        this.status = status;
        this.description = description;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}