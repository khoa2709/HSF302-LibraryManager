package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/books")
    public String showBooks(@RequestParam(value = "keyword", required = false, defaultValue = "") String kw, Model model, HttpSession session ) {

        User account = (User) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }
         if (account.getRole().equals("admin")) {
             if (kw.equals("")) {
                 model.addAttribute("books", bookService.findAll());
             } else {
                 model.addAttribute("books", bookService.searchBooks(kw));
             }
         } else {
             if(kw.equals("")) {
                 model.addAttribute("books", bookService.findActiveBooks());
             } else {
                 model.addAttribute("books", bookService.searchActiveBooks(kw));
             }
         }
        return "books";
    }

    @PostMapping("/books/save")
    public String save(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model, @RequestParam(
            "formMode") String formMode) {
        if (result.hasErrors()) {

            model.addAttribute("books", bookService.findAll());

            model.addAttribute("formMode", formMode);

            return "book-form";
        }

        if (formMode.equals("new")) {
            if (bookService.existsByTitle(book.getTitle())) {
                model.addAttribute("books", bookService.findAll() );

                model.addAttribute("formMode", formMode);
                model.addAttribute("duplicated", "Duplicated title. Input another one!");
                return "book-form";
            }
        }

        bookService.save(book);

        return "redirect:/books";
    }

    @GetMapping("/books/add")
    public String showBookForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("book", new Book());

        model.addAttribute("cates", categoryService.findAll());

        model.addAttribute("formMode", "new");

        return "/book-form";
    }


    @GetMapping("/books/edit/{id}")
    public String editBook(Model model, HttpSession session, @PathVariable Long id){
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Book book = bookService.findById(id).get();

        model.addAttribute("book", book);

        model.addAttribute("cates", categoryService.findAll());

        model.addAttribute("formMode", "edit");

        return "/book-form";
    }

    @GetMapping("/books/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession ss) {

        User acc = (User) ss.getAttribute("loggedInUser");
        if (acc == null) {
            return "redirect:/login";
        }

        if (!acc.getRole().equals("admin")) {
            return "redirect:/books";
        }

        bookService.deleteById(id);

        return "redirect:/books";
    }

}
