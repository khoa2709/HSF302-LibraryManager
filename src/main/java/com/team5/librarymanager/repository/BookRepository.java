package com.team5.librarymanager.repository;

import com.team5.librarymanager.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findAllByStatus(boolean status);

    List<Book> findAllByStatusAndTitleContainingIgnoreCase(boolean b, String keyword);
}