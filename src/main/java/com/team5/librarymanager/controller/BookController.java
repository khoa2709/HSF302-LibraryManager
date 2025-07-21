package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/books")
    public String showBooks(@RequestParam(value = "keyword", required = false, defaultValue = "") String kw, Model model, HttpSession session ) {

        if (!kw.equals("")) {
            model.addAttribute("books", bookService.searchBooks(kw));
        }else {
            model.addAttribute("books", bookService.findAll());
        }

        User account = (User) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }
        return "books";
    }

}
