package com.team5.librarymanager.controller;

import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.CategoryService;
import com.team5.librarymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.team5.librarymanager.service.LoanService;

@Controller
public class ReportController {
    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/report")
    public String showReport(Model model) {
        Long borrowed = loanService.countCurrentLoans();
        Long overdue = loanService.countOverdueLoans(); 
        Long booksActive = bookService.findAll().stream().filter(book -> book.isStatus()).count();
        Long usersActive = userService.countNormalUsers();
        Long catesActive = categoryService.findAll().stream().filter(cate -> cate.isStatus()).count();
        Long usersInactive = userService.findAll().stream().filter(user -> !user.isStatus()).count();
        Long booksInactive = bookService.findAll().stream().filter(book -> !book.isStatus()).count();
        Long catesInactive = categoryService.findAll().stream().filter(cate -> !cate.isStatus()).count();
        model.addAttribute("borrowed", borrowed);
        model.addAttribute("overdue", overdue);
        model.addAttribute("booksActive", booksActive);
        model.addAttribute("usersActive", usersActive);
        model.addAttribute("catesActive", catesActive);
        model.addAttribute("usersInactive", usersInactive);
        model.addAttribute("booksInactive", booksInactive);
        model.addAttribute("catesInactive", catesInactive);
        return "report";
    }
}
