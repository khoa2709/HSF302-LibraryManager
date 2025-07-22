package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Book> findActiveBooks() {
        return bookRepository.findAllByStatus(true);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> searchActiveBooks(String keyword) {
        return bookRepository.findAllByStatusAndTitleContainingIgnoreCase(true, keyword);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        Book book = bookRepository.findById(id).get();
        book.setStatus(false);
        bookRepository.save(book);
    }

    public Long count() {
        return bookRepository.count();
    }

    public void saveAll(List<Book> list) {
        bookRepository.saveAll(list);
    }

    public boolean existsByTitle(String title) {
        return bookRepository.existsByTitle(title);
    }
}